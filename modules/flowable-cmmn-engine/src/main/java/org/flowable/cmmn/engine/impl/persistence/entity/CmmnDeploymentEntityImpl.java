/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.cmmn.engine.impl.persistence.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.util.CommandContextUtil;
import org.flowable.common.engine.api.repository.EngineResource;
import org.flowable.common.engine.impl.persistence.entity.AbstractEntityNoRevision;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class CmmnDeploymentEntityImpl extends AbstractEntityNoRevision implements CmmnDeploymentEntity, Serializable {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected String category;
    protected String key;
    protected String tenantId = CmmnEngineConfiguration.NO_TENANT_ID;
    protected String parentDeploymentId;
    protected Map<String, EngineResource> resources;
    protected Date deploymentTime;
    protected boolean isNew;

    /**
     * Will only be used during actual deployment to pass deployed artifacts (eg process definitions). Will be null otherwise.
     */
    protected Map<Class<?>, List<Object>> deployedArtifacts;

    public CmmnDeploymentEntityImpl() {

    }

    public void addResource(CmmnResourceEntity resource) {
        if (resources == null) {
            resources = new HashMap<>();
        }
        resources.put(resource.getName(), resource);
    }

    public Map<String, EngineResource> getResources() {
        if (resources == null && id != null) {
            List<CmmnResourceEntity> resourcesList = CommandContextUtil.getCmmnResourceEntityManager().findResourcesByDeploymentId(id);
            resources = new HashMap<>();
            for (CmmnResourceEntity resource : resourcesList) {
                resources.put(resource.getName(), resource);
            }
        }
        return resources;
    }

    public Object getPersistentState() {
        Map<String, Object> persistentState = new HashMap<>();
        persistentState.put("category", this.category);
        persistentState.put("tenantId", tenantId);
        return persistentState;
    }

    // Deployed artifacts manipulation ////////////////////////////////////////////

    public void addDeployedArtifact(Object deployedArtifact) {
        if (deployedArtifacts == null) {
            deployedArtifacts = new HashMap<>();
        }

        Class<?> clazz = deployedArtifact.getClass();
        List<Object> artifacts = deployedArtifacts.get(clazz);
        if (artifacts == null) {
            artifacts = new ArrayList<>();
            deployedArtifacts.put(clazz, artifacts);
        }

        artifacts.add(deployedArtifact);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getDeployedArtifacts(Class<T> clazz) {
        for (Class<?> deployedArtifactsClass : deployedArtifacts.keySet()) {
            if (clazz.isAssignableFrom(deployedArtifactsClass)) {
                return (List<T>) deployedArtifacts.get(deployedArtifactsClass);
            }
        }
        return null;
    }

    // getters and setters ////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getParentDeploymentId() {
        return parentDeploymentId;
    }

    public void setParentDeploymentId(String parentDeploymentId) {
        this.parentDeploymentId = parentDeploymentId;
    }

    public void setResources(Map<String, EngineResource> resources) {
        this.resources = resources;
    }

    public Date getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(Date deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
    
    @Override
    public String getDerivedFrom() {
        return null;
    }

    @Override
    public String getDerivedFromRoot() {
        return null;
    }

    public String getEngineVersion() {
        return null;
    }

    // common methods //////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "CmmnDeploymentEntity[id=" + id + ", name=" + name + "]";
    }

}
