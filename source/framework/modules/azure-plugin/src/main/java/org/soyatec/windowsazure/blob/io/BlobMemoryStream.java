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
package org.soyatec.windowsazure.blob.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Implementation of BlobStream with data storage in memory
 *
 * @author yyang
 *
 */
public class BlobMemoryStream implements BlobStream {

	private ByteBuffer bytes;
	// private ByteArrayInputStream in;
	private ByteArrayOutputStream stream;

	public BlobMemoryStream(byte[] buf) {
		bytes = ByteBuffer.wrap(buf);
		// in = new ByteArrayInputStream( value);
	}

	public BlobMemoryStream(InputStream input) throws IOException {
		byte[] block = new byte[1024];
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream(input.available());
			while (true) {
				int length = input.read(block);
				if(length < 0)
					break;
				buf.write(block, 0, length);

			}
			bytes = ByteBuffer.wrap(buf.toByteArray());
		}finally{
			input.close();
		}
	}

	public BlobMemoryStream() {
		stream = new ByteArrayOutputStream();
	}

	public BlobMemoryStream(byte[] buf, int offset, int length) {
		bytes = ByteBuffer.wrap(buf, offset, length);
	}

	public void close() throws IOException {
		if (bytes != null)
			bytes.clear();
		// if( in != null) in.close();

		if (stream != null)
			stream.close();
	}

	public long length() throws IOException {
		if (bytes != null)
			return bytes.limit();
		// if(in != null) return in.available();
		else
			return stream.size();

	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public int read(byte[] buffer, int offset, int len) throws IOException {
		if (bytes != null) {
			int length = bytes.remaining();
			if (length > len)
				length = len;
			bytes.get(buffer, offset, length);
			return length;
		} else {
			throw new IOException("The stream is not readable.");
		}
	}

	public void write(byte[] buffer, int offset, int len) throws IOException {
		if (stream != null)
			stream.write(buffer, offset, len);
		else
			throw new IOException("The stream is not writable.");

	}

	public boolean canSeek() {
		if (bytes != null)
			return true;
		else
			return false;
	}

	public byte[] getBytes() throws IOException {
		if (stream != null)
			return stream.toByteArray();
		else
			return bytes.array();
	}

	public long getPosition() throws IOException {
		if (bytes != null)
			return bytes.position();
		else
			throw new IOException("The stream is not seekable.");
	}

	public void setPosition(long position) throws IOException {
		if (bytes != null)
			bytes.position((int) position);
		else
			throw new IOException("The stream is not seekable.");
	}
}
