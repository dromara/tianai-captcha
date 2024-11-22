package cloud.tianai.captcha.resource;

import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResourceStore implements ResourceStore {

    @Getter
    protected ChainListener listener = new ChainListener();

    boolean isInit = false;

    @Override
    public void init(ImageCaptchaResourceManager resourceManager) {
        if (isInit) {
            return;
        }
        doInit();
        isInit = true;
        listener.onInit(this, resourceManager);
    }

    @Override
    public void addListener(ResourceListener listener) {
        this.listener.removeListener(listener);
        this.listener.addListener(listener);
    }

    @Override
    public void addResource(String type, Resource resource) {
        doAddResource(type, resource);
        if (isInit) {
            listener.onAddResource(type, resource);
        }
    }

    @Override
    public void addTemplate(String type, ResourceMap template) {
        doAddTemplate(type, template);
        if (isInit) {
            listener.onAddTemplate(type, template);
        }
    }

    @Override
    public Resource deleteResource(String type, String id) {
        Resource resource = doDeleteResource(type, id);
        if (isInit && resource != null) {
            listener.onDeleteResource(type, resource);
        }
        return resource;
    }

    @Override
    public ResourceMap deleteTemplate(String type, String id) {
        ResourceMap resourceMap = doDeleteTemplate(type, id);
        if (isInit && resourceMap != null) {
            listener.onDeleteTemplate(type, resourceMap);
        }
        return resourceMap;
    }

    @Override
    public Resource randomGetResourceByTypeAndTag(String type, String tag) {
        Resource resource = doRandomGetResourceByTypeAndTag(type, tag);
        if (isInit && resource != null) {
            listener.onRandomGetResourceByTypeAndTag(type, tag, resource);
        }
        return resource;
    }

    @Override
    public ResourceMap randomGetTemplateByTypeAndTag(String type, String tag) {
        ResourceMap resourceMap = doRandomGetTemplateByTypeAndTag(type, tag);
        if (isInit && resourceMap != null) {
            listener.onRandomGetTemplateByTypeAndTag(type, tag, resourceMap);
        }
        return resourceMap;
    }


    @Override
    public void clearAllResources() {
        doClearAllResources();
        if (isInit) {
            listener.onClearAllResources();
        }
    }

    @Override
    public void clearAllTemplates() {
        doClearAllTemplates();
        if (isInit) {
            listener.onClearAllTemplates();
        }
    }

    public void doInit() {

    }

    public abstract void doClearAllResources();

    public abstract void doClearAllTemplates();

    public abstract Resource doRandomGetResourceByTypeAndTag(String type, String tag);

    public abstract ResourceMap doRandomGetTemplateByTypeAndTag(String type, String tag);

    public abstract ResourceMap doDeleteTemplate(String type, String id);

    public abstract Resource doDeleteResource(String type, String id);

    public abstract void doAddResource(String type, Resource resource);

    public abstract void doAddTemplate(String type, ResourceMap template);


    public static class ChainListener implements ResourceListener {
        protected List<ResourceListener> listeners = new ArrayList<>();

        public ChainListener() {

        }

        public void addListener(ResourceListener listener) {
            listeners.add(listener);
        }

        public void removeListener(ResourceListener listener) {
            listeners.remove(listener);
        }

        @Override
        public void onInit(ResourceStore resourceStore, ImageCaptchaResourceManager resourceManager) {
            for (ResourceListener listener : listeners) {
                listener.onInit(resourceStore, resourceManager);
            }
        }

        @Override
        public void onAddResource(String type, Resource resource) {
            for (ResourceListener listener : listeners) {
                listener.onAddResource(type, resource);
            }
        }

        @Override
        public void onAddTemplate(String type, ResourceMap template) {
            for (ResourceListener listener : listeners) {
                listener.onAddTemplate(type, template);
            }
        }

        @Override
        public void onClearAllResources() {
            for (ResourceListener listener : listeners) {
                listener.onClearAllResources();
            }
        }

        @Override
        public void onClearAllTemplates() {
            for (ResourceListener listener : listeners) {
                listener.onClearAllTemplates();
            }
        }

        @Override
        public void onRandomGetResourceByTypeAndTag(String type, String tag, Resource resource) {
            for (ResourceListener listener : listeners) {
                listener.onRandomGetResourceByTypeAndTag(type, tag, resource);
            }
        }

        @Override
        public void onRandomGetTemplateByTypeAndTag(String type, String tag, ResourceMap template) {
            for (ResourceListener listener : listeners) {
                listener.onRandomGetTemplateByTypeAndTag(type, tag, template);
            }
        }

        @Override
        public void onDeleteResource(String type, Resource resource) {
            for (ResourceListener listener : listeners) {
                listener.onDeleteResource(type, resource);
            }
        }

        @Override
        public void onDeleteTemplate(String type, ResourceMap template) {
            for (ResourceListener listener : listeners) {
                listener.onDeleteTemplate(type, template);
            }
        }


    }
}
