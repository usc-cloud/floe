/**
 * Copyright  2006-2010 Soyatec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.soyatec.windowsazure.blob.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.soyatec.windowsazure.authenticate.Base64;
import org.soyatec.windowsazure.blob.BlobType;
import org.soyatec.windowsazure.blob.BlockListType;
import org.soyatec.windowsazure.blob.IBlobContainer;
import org.soyatec.windowsazure.blob.IBlobContents;
import org.soyatec.windowsazure.blob.IBlobProperties;
import org.soyatec.windowsazure.blob.IBlockBlob;
import org.soyatec.windowsazure.blob.IBlockList;
import org.soyatec.windowsazure.blob.IRetryPolicy;
import org.soyatec.windowsazure.blob.io.BlobMemoryStream;
import org.soyatec.windowsazure.blob.io.BlobStream;
import org.soyatec.windowsazure.constants.BlobBlockConstants;
import org.soyatec.windowsazure.constants.XmlElementNames;
import org.soyatec.windowsazure.error.StorageException;
import org.soyatec.windowsazure.internal.ResourceUriComponents;
import org.soyatec.windowsazure.internal.constants.CompConstants;
import org.soyatec.windowsazure.internal.constants.HeaderNames;
import org.soyatec.windowsazure.internal.constants.HttpMethod;
import org.soyatec.windowsazure.internal.constants.HttpWebResponse;
import org.soyatec.windowsazure.internal.constants.QueryParams;
import org.soyatec.windowsazure.internal.constants.XmsVersion;
import org.soyatec.windowsazure.internal.util.HttpUtilities;
import org.soyatec.windowsazure.internal.util.Logger;
import org.soyatec.windowsazure.internal.util.NameValueCollection;
import org.soyatec.windowsazure.internal.util.Utilities;
import org.soyatec.windowsazure.internal.util.ssl.SslUtil;
import org.soyatec.windowsazure.internal.util.xml.XPathQueryHelper;
import org.soyatec.windowsazure.internal.util.xml.XmlUtil;

public class BlockBlob extends Blob implements IBlockBlob {

	private static final int BLOB_ACTION = 1;
	private static final int BLOCK_ACTION = 2;
	private static final int BLOCK_LIST_ACTION = 4;

	BlockBlob(BlobContainerRest container, String blobName) {
		super(container, blobName);
	}

	public void setContents(IBlobContents contents) throws StorageException {
		try {
			putBlobImpl(new BlobProperties(blobName), contents.getStream(),
					true, null);
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	boolean putBlobImpl(final IBlobProperties blobProperties,
			final BlobStream stream, final boolean overwrite, final String eTag)
			throws Exception {
		if (blobProperties == null) {
			throw new IllegalArgumentException(
					"Blob properties cannot be null or empty!");
		}

		if (stream == null) {
			throw new IllegalArgumentException(
					"Stream cannot be null or empty!");
		}

		if (container.getName().equals(IBlobContainer.ROOT_CONTAINER)
				&& blobProperties.getName().indexOf('/') > -1) {
			throw new IllegalArgumentException(
					"Blobs stored in the root container can not have a name containing a forward slash (/).");
		}

		// If the blob is large, we should use blocks to upload it in pieces.
		// This will ensure that a broken connection will only impact a single
		// piece
		final long originalPosition = stream.getPosition();
		final long length = stream.length() - stream.getPosition();
		if (length > BlobBlockConstants.MaximumBlobSizeBeforeTransmittingAsBlocks) {
			return putLargeBlobImpl(blobProperties, stream, overwrite, eTag);
		}

		boolean retval = false;
		IRetryPolicy policy = stream.canSeek() ? container.getRetryPolicy()
				: RetryPolicies.noRetry();
		retval = (Boolean) policy.execute(new Callable<Boolean>() {

			public Boolean call() throws Exception {
				if (stream.canSeek()) {
					stream.setPosition(originalPosition);
				}

				return uploadData(blobProperties, stream, length, overwrite,
						eTag, new NameValueCollection(), BLOB_ACTION);
			}

		});

		return retval;
	}

	boolean uploadData(IBlobProperties blobProperties, BlobStream stream,
			long length, boolean overwrite, String eTag,
			NameValueCollection queryParameters, int action) throws Exception {

		// fix root container
		boolean isRoot = container.getName().equals(
				IBlobContainer.ROOT_CONTAINER);
		String containerName = isRoot ? "" : container.getName();
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				container.getAccountName(), containerName,
				blobProperties.getName());
		URI blobUri = HttpUtilities.createRequestUri(container.getBaseUri(),
				container.isUsePathStyleUris(), container.getAccountName(),
				containerName, blobProperties.getName(),
				container.getTimeout(), queryParameters, uriComponents,
				container.getCredentials());

		if (SSLProperties.isSSL()) {
			try {
				URI newBlobUri = new URI("https", null, blobUri.getHost(), 443,
						blobUri.getPath(), blobUri.getQuery(),
						blobUri.getFragment());
				blobUri = newBlobUri;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HttpRequest request = createHttpRequestForPutBlob(blobUri,
				HttpMethod.Put, blobProperties, length, overwrite, eTag);
		// if (isRoot) {
		// request.addHeader(HeaderNames.ApiVersion,
		// XmsVersion.VERSION_2009_07_17);
		// }
		request.setHeader(HeaderNames.ApiVersion, XmsVersion.VERSION_2009_09_19);
		if (action == BLOB_ACTION || action == BLOCK_ACTION) { // small blob or
																// block
			request.addHeader(HeaderNames.BlobType,
					BlobType.BlockBlob.getLiteral());
		}

		boolean retval = false;
		BlobStream requestStream = new BlobMemoryStream();
		Utilities.copyStream(stream, requestStream, (int) length);
		byte[] body = requestStream.getBytes();
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body));

		processMD5(blobProperties, request, body, action);

		container.credentials.signRequest(request, uriComponents);

		HttpWebResponse response = null;
		if (SSLProperties.isSSL()) {
			SSLSocketFactory factory = SslUtil.createSSLSocketFactory(
					SSLProperties.getKeyStore(),
					SSLProperties.getKeyStorePasswd(),
					SSLProperties.getTrustStore(),
					SSLProperties.getTrustStorePasswd(),
					SSLProperties.getKeyAlias());
			response = HttpUtilities.getSSLReponse((HttpUriRequest) request,
					factory);
		} else {
			response = HttpUtilities.getResponse(request);
		}
		if (response.getStatusCode() == HttpStatus.SC_CREATED) {
			retval = true;
		} else if (!overwrite
				&& (response.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED || response
						.getStatusCode() == HttpStatus.SC_NOT_MODIFIED)) {
			retval = false;
		} else {
			retval = false;
			HttpUtilities.processUnexpectedStatusCode(response);
		}

		blobProperties.setLastModifiedTime(response.getLastModified());
		blobProperties.setETag(response.getHeader(HeaderNames.ETag));
		requestStream.close();
		response.close();
		return retval;
	}

	public void processMD5(IBlobProperties blobProperties, HttpRequest request,
			byte[] body, int action) {
		if (blobProperties.getContentMD5() != null) {
			addHeaderMD5(blobProperties, request, action,
					blobProperties.getContentMD5());
		} else {
			if (BlobBlockConstants.GenerateBlockMD5
					&& (action == BLOCK_ACTION || action == BLOCK_LIST_ACTION)) {
				// compute md5 for request body
				try {
					addHeaderMD5(blobProperties, request, action,
							Utilities.MD5(body));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addHeaderMD5(IBlobProperties blobProperties,
			HttpRequest request, int action, String md5) {
		if (action == BLOCK_LIST_ACTION)
			request.addHeader(HeaderNames.BlobContentMD5, md5); // large blob
		else
			request.addHeader(HeaderNames.ContentMD5, md5); // small blob
	}

	private HttpRequest createHttpRequestForPutBlob(URI blobUri,
			String httpMethod, IBlobProperties blobProperties,
			long contentLength, boolean overwrite, String eTag) {
		HttpRequest request = HttpUtilities.createHttpRequestWithCommonHeaders(
				blobUri, httpMethod, container.getTimeout());
		if (blobProperties.getContentEncoding() != null) {
			request.addHeader(HeaderNames.ContentEncoding,
					blobProperties.getContentEncoding());
		}
		if (blobProperties.getContentLanguage() != null) {
			request.addHeader(HeaderNames.ContentLanguage,
					blobProperties.getContentLanguage());
		}
		if (blobProperties.getContentType() != null) {
			request.addHeader(HeaderNames.ContentType,
					blobProperties.getContentType());
		}
		if (eTag != null) {
			request.addHeader(HeaderNames.IfMatch, eTag);
		}

		if (blobProperties.getMetadata() != null
				&& blobProperties.getMetadata().size() > 0) {
			HttpUtilities.addMetadataHeaders(request,
					blobProperties.getMetadata());
		}
		// request.addHeader(HeaderNames.ContentLength,
		// String.valueOf(contentLength));

		if (!overwrite) {
			request.addHeader(HeaderNames.IfNoneMatch, "*");
		}
		return request;
	}

	boolean putLargeBlobImpl(final IBlobProperties blobProperties,
			final BlobStream stream, final boolean overwrite, final String eTag)
			throws Exception {
		boolean retval = false;
		// Since we got a large block, chunk it into smaller pieces called
		// blocks
		// final long blockSize = BlobBlockConstants.BlockSize;
		final long startPosition = stream.getPosition();
		final long length = stream.length() - startPosition;
		int numBlocks = (int) Math.ceil((double) length / blockSize);
		String[] blockIds = new String[numBlocks];

		// We can retry only if the stream supports seeking. An alternative is
		// to buffer the data in memory
		// but we do not do this currently.
		IRetryPolicy policy = stream.canSeek() ? container.getRetryPolicy()
				: RetryPolicies.noRetry();
		// Upload each of the blocks, retrying any failed uploads
		String md5 = blobProperties.getContentMD5();
		blobProperties.setContentMD5(null);
		
		final String[] originalIds = new String[numBlocks];
		final long[] blockLengths = new long[numBlocks]; 
		for (int i = 0; i < numBlocks; ++i) {
			String generateBlockId = generateBlockId(i);
			final String blockId = Base64.encode(generateBlockId
					.getBytes("UTF-8"));
			blockIds[i] = blockId;
			originalIds[i] = generateBlockId;
			Logger.debug("Base64 encoded Block Id:" + blockIds[i]);
			final int index = i;
			retval = (Boolean) policy.execute(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					// Rewind the stream to appropriate location in case this is
					// a retry
					if (stream.canSeek()) {
						stream.setPosition(startPosition + index * blockSize);
					}
					NameValueCollection params = new NameValueCollection();
					params.put(QueryParams.QueryParamComp, CompConstants.Block);
					params.put(QueryParams.QueryParamBlockId, blockId);
					long blockLength = Math.min(blockSize,
							length - stream.getPosition());
					blockLengths[index ] = blockLength;
					return uploadData(blobProperties, stream, blockLength,
							overwrite, eTag, params, BLOCK_ACTION);

				}
			});
		}
		blobProperties.setContentMD5(md5);
		retval = putBlockListImpl(blobProperties, blockIds, overwrite, eTag);

		if(BlobBlockConstants.GenerateBlockMD5){			
			verifyBlockList(originalIds, blockLengths);
		}
		return retval;
	}

	private void verifyBlockList( String[] blockIds,
			long[] blockLengths) {
		IBlockList list = getBlockList();
		if(list.getUncommittedBlocks().size() > 0)
			throw new StorageException("Some blocks are not committed.");
		
		if(list.getCommittedBlocks().size() != blockIds.length)
			throw new StorageException("Some blocks are not committed.");

		for(int i = 0; i < blockIds.length; i ++){
			Block b = list.getCommittedBlocks().get(i);
			if( !b.getName().equals( blockIds[i]) || blockLengths[i] != Long.valueOf( b.getSize()) ){			
				throw new StorageException("Block with id " +  blockIds[i] + " is corrupted.");
			}
		}
			
	}

	boolean putBlockListImpl(final IBlobProperties blobProperties,
			String[] blockIds, final boolean overwrite, final String eTag)
			throws Exception, IOException {
		boolean retval;
		// Now commit the list
		// First create the output
		Document doc = DocumentHelper.createDocument();
		Element blockListElement = doc.addElement(XmlElementNames.BlockList);
		for (String id : blockIds) {
			blockListElement.addElement(XmlElementNames.BlockUncommitted)
					.setText(id);
		}

		NameValueCollection params = new NameValueCollection();
		params.put(QueryParams.QueryParamComp, CompConstants.BlockList);
		BlobStream buffer = new BlobMemoryStream(doc.asXML().getBytes());
		retval = uploadData(blobProperties, buffer, buffer.length(), overwrite,
				eTag, params, BLOCK_LIST_ACTION);
		return retval;
	}

	/**
	 * For a given blob, the length of the value specified for the blockid
	 * parameter must be the same size for each block.
	 * 
	 * For more, see <a
	 * href="http://msdn.microsoft.com/en-us/library/dd135726.aspx">Put
	 * block</a>
	 * 
	 * 
	 * @param i
	 * @return
	 */
	String generateBlockId(int i) {
		String value = String.valueOf(i);
		while (value.length() < 64) {
			value = "0" + value;
		}
		return value;
	}

	public boolean updateIfNotModified(IBlobProperties blobProperties,
			IBlobContents contents) throws StorageException {
		try {
			if (blobProperties == null)
				blobProperties = new BlobProperties(blobName);
			if (contents != null) {
				return putBlobImpl(blobProperties, contents.getStream(), true,
						blobProperties.getETag());
			} else {
				return setPropertiesImpl(blobProperties,
						blobProperties.getETag());
			}
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	public void putBlockList(List<String> blockList) {
		try {
			putBlockListImpl(new BlobProperties(blobName),
					blockList.toArray(new String[blockList.size()]), true, null);
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	public void putBlock(String blockId, IBlobContents contents) {
		NameValueCollection params = new NameValueCollection();
		params.put(QueryParams.QueryParamComp, CompConstants.Block);
		params.put(QueryParams.QueryParamBlockId, blockId);
		BlobStream stream = contents.getStream();
		try {
			uploadData(new BlobProperties(blobName), stream, stream.length(),
					true, null, params, BLOCK_ACTION);
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	public IBlockList getBlockList() {
		return getBlockList(BlockListType.All, null);
	}

	public IBlockList getBlockList(BlockListType type, String snapshot) {
		if (type == null)
			type = BlockListType.All;

		String containerName = getContainerName();
		NameValueCollection queryParams = new NameValueCollection();
		queryParams.put(QueryParams.QueryParamComp, CompConstants.BlockList);
		if (snapshot != null) {
			queryParams.put(CompConstants.Snapshot, snapshot);
		}

		final ResourceUriComponents uriComponents = new ResourceUriComponents(
				container.getAccountName(), containerName, blobName);
		final URI uri = HttpUtilities.createRequestUri(container.getBaseUri(),
				container.isUsePathStyleUris(), container.getAccountName(),
				containerName, blobName, container.getTimeout(), queryParams,
				uriComponents, container.getCredentials());

		IBlockList blockList = (IBlockList) container.getRetryPolicy().execute(
				new Callable<IBlockList>() {

					public IBlockList call() throws Exception {
						HttpRequest request = HttpUtilities
								.createHttpRequestWithCommonHeaders(uri,
										HttpMethod.Get, container.getTimeout());

						request.addHeader(HeaderNames.ApiVersion,
								XmsVersion.VERSION_2009_09_19);
						container.getCredentials().signRequest(request,
								uriComponents);
						HttpWebResponse response = HttpUtilities
								.getResponse(request);
						int statusCode = response.getStatusCode();
						if (statusCode == HttpStatus.SC_OK) {
							return blockListFromResponse(response.getStream());
						} else if (response.getStatusCode() == HttpStatus.SC_GONE
								|| response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
							response.close();
							return null;
						} else {
							HttpUtilities.processUnexpectedStatusCode(response);
							return null;
						}
					}
				});

		return blockList;
	}


	static IBlockList blockListFromResponse(InputStream stream) {

		BlockList list = new BlockList();
		Document document = XmlUtil.load(stream);
		List xmlNodes = document
				.selectNodes(XPathQueryHelper.CommittedBlocksQuery);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			list.addCommittedBlock(createBlock((Element) iterator.next()));
		}

		xmlNodes = document
				.selectNodes(XPathQueryHelper.UncommittedBlocksQuery);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			list.addUncommittedBlock(createBlock((Element) iterator.next()));
		}
		return list;
	}

	static Block createBlock(Element node) {
		String base64 = XPathQueryHelper.loadSingleChildStringValue(node,
				XmlElementNames.BlockName, false);
		String size = XPathQueryHelper.loadSingleChildStringValue(node,
				XmlElementNames.BlockSize, false);
		String name = base64 == null ? base64 : new String( Base64.decode(base64) ) ;
		Block block = new Block(name, size);
		return block;
	}

}
