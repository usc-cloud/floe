/*
 *  Copyright 2013 University of Southern California
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.package edu.usc.goffish.gopher.sample;
 */
package edu.usc.pgroup.floe.impl.manager.infraManager.infraHandler;

import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceRequest;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.AcquireResourceResponse;
import edu.usc.pgroup.floe.api.framework.manager.infraManager.ResourceIdentifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GopherInfraHandler extends LocalClusterInfraHandler {

    private Map<String, LocalResource> resources = new HashMap<>();


    public static final String PARTITION = "partition";

    @Override
    public AcquireResourceResponse acquireResource(AcquireResourceRequest resourceRequest) {


        Node.ResourceInfo resourceInfo = resourceRequest.getResourceInfo();
        String part = resourceInfo.getOptionalConfiguration().getParams().get(PARTITION);
        synchronized (resources) {
            if (resources.containsKey(part)) {

                LocalResource resource = resources.get(part);
                if(resource != null) {
                    if(resource.getNumberOfCores() >= resourceInfo.getNumberOfCores()) {
                        AcquireResourceResponse response = new AcquireResourceResponse();

                        response.setResourceId(resource.getResourceId());
                        response.setRequesetId(resourceRequest.getRequestId());
                        response.setResourceInfo((Node.ResourceInfo) resource);
                        resource.setNumberOfCores(resource.getNumberOfCores() -
                                resourceInfo.getNumberOfCores());
                        return response;
                    }
                }

            } else {
                if("N/A".equalsIgnoreCase(part)) {

                    for(Iterator<LocalResource> iterator = resources.values().iterator();iterator.hasNext();) {

                        LocalResource resource  = iterator.next();
                        if(resource.getNumberOfCores() >= resourceInfo.getNumberOfCores()) {
                            AcquireResourceResponse response = new AcquireResourceResponse();

                            response.setResourceId(resource.getResourceId());
                            response.setRequesetId(resourceRequest.getRequestId());
                            response.setResourceInfo((Node.ResourceInfo) resource);
                            resource.setNumberOfCores(resource.getNumberOfCores() -
                                    resourceInfo.getNumberOfCores());
                            return response;
                        }
                    }

                }

            }
        }

        return null;
    }

    @Override
    public ResourceIdentifier registerResource(Node.ResourceInfo resource, String host) {
        LocalResource localResource = new LocalResource(resource);

        ResourceIdentifier rid = ResourceIdentifier.getNewId();
        rid.setHost(host);
        localResource.setResourceId(rid);

        String part = resource.getOptionalConfiguration().getParams().get(PARTITION);
        resources.put(part, localResource);

        return rid;
    }
}
