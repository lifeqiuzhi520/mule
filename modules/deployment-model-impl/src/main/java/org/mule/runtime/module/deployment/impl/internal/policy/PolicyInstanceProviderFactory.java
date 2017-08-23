/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.deployment.impl.internal.policy;

import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.context.notification.MuleContextListener;
import org.mule.runtime.core.api.policy.PolicyParametrization;
import org.mule.runtime.deployment.model.api.application.Application;
import org.mule.runtime.deployment.model.api.policy.PolicyTemplate;

/**
 * Creates {@link ApplicationPolicyInstance} instances
 */
public interface PolicyInstanceProviderFactory {

  /**
   * Creates a new policy
   *
   * @param application application when the policy is applied. Non null
   * @param policyTemplate template of the policy being applied. Non null.
   * @param parametrization parameters used to configure the template. Non null.
   * @param muleContextListener listener to be used during the creation of a the policy's {@link MuleContext}
   * @return
   */
  ApplicationPolicyInstance create(Application application, PolicyTemplate policyTemplate, PolicyParametrization parametrization,
                                   MuleContextListener muleContextListener);
}
