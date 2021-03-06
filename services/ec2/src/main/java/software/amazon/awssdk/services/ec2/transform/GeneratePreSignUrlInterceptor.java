/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.services.ec2.transform;

import java.net.URI;
import software.amazon.awssdk.core.AmazonClientException;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.auth.Aws4Signer;
import software.amazon.awssdk.core.http.SdkHttpFullRequestAdapter;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.regions.Region;
import software.amazon.awssdk.core.util.AwsHostNameUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.services.ec2.EC2Client;
import software.amazon.awssdk.services.ec2.model.CopySnapshotRequest;

/**
 * ExecutionInterceptor that generates a pre-signed URL for copying encrypted snapshots
 * TODO: Is this actually right? What if a different interceptor modifies the message? Should this be treated as a signer?
 */
public class GeneratePreSignUrlInterceptor implements ExecutionInterceptor {

    @Override
    public SdkHttpFullRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        SdkHttpFullRequest request = context.httpRequest();
        SdkRequest originalRequest = context.request();

        if (originalRequest instanceof CopySnapshotRequest) {

            CopySnapshotRequest originalCopySnapshotRequest = (CopySnapshotRequest) originalRequest;

            // Return if presigned url is already specified by the user.
            if (originalCopySnapshotRequest.presignedUrl() != null) {
                return request;
            }

            String serviceName = "ec2";

            // The source regions where the snapshot currently resides.
            String sourceRegion = originalCopySnapshotRequest.sourceRegion();
            String sourceSnapshotId = originalCopySnapshotRequest
                    .sourceSnapshotId();

            /*
             * The region where the snapshot has to be copied from the source.
             * The original copy snap shot request will have the end point set
             * as the destination region in the client before calling this
             * request.
             */
            String destinationRegion = originalCopySnapshotRequest
                                               .destinationRegion() != null ? originalCopySnapshotRequest
                    .destinationRegion() : AwsHostNameUtils
                    .parseRegionName(request.host(), serviceName);

            URI endPointSource = createEndpoint(sourceRegion, serviceName);

            SdkHttpFullRequest requestForPresigning = generateRequestForPresigning(
                    sourceSnapshotId, sourceRegion, destinationRegion)
                    .toBuilder()
                    .protocol(endPointSource.getScheme())
                    .host(endPointSource.getHost())
                    .port(endPointSource.getPort())
                    .method(SdkHttpMethod.GET)
                    .build();

            Aws4Signer signer = new Aws4Signer();
            signer.setServiceName(serviceName);

            InterceptorContext newExecutionContext = InterceptorContext.builder()
                                                                       .request(originalRequest)
                                                                       .httpRequest(requestForPresigning)
                                                                       .build();

            final SdkHttpFullRequest presignedRequest =
                    signer.presign(newExecutionContext, executionAttributes, null);

            return request.toBuilder()
                          .rawQueryParameter("DestinationRegion", destinationRegion)
                          .rawQueryParameter("PresignedUrl", presignedRequest.getUri().toString())
                          .build();
        }

        return request;

    }

    /**
     * Generates a Request object for the pre-signed URL.
     */
    private SdkHttpFullRequest generateRequestForPresigning(String sourceSnapshotId,
                                                            String sourceRegion,
                                                            String destinationRegion) {

        CopySnapshotRequest copySnapshotRequest = CopySnapshotRequest.builder()
                                                                     .sourceSnapshotId(sourceSnapshotId)
                                                                     .sourceRegion(sourceRegion)
                                                                     .destinationRegion(destinationRegion)
                                                                     .build();

        return SdkHttpFullRequestAdapter.toHttpFullRequest(new CopySnapshotRequestMarshaller().marshall(copySnapshotRequest));
    }

    private URI createEndpoint(String regionName, String serviceName) {

        final Region region = Region.of(regionName);

        if (region == null) {
            throw new AmazonClientException("{" + serviceName + ", " + regionName + "} was not "
                                            + "found in region metadata. Update to latest version of SDK and try again.");
        }

        return EC2Client.serviceMetadata().endpointFor(region);
    }
}
