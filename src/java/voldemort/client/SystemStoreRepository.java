package voldemort.client;

import java.util.concurrent.ConcurrentHashMap;

import voldemort.cluster.failuredetector.FailureDetector;
import voldemort.store.system.SystemStoreConstants;

/*
 * A repository that creates and maintains all the system stores in one place.
 * The purpose is to act as a source of truth for all the system stores, since
 * they can be recreated dynamically (in case cluster.xml changes).
 */

public class SystemStoreRepository {

    private ConcurrentHashMap<String, SystemStore> sysStoreMap;

    public SystemStoreRepository() {
        sysStoreMap = new ConcurrentHashMap<String, SystemStore>();
    }

    public void addSystemStore(SystemStore newSysStore, String storeName) {
        this.sysStoreMap.put(storeName, newSysStore);
    }

    public void createSystemStores(ClientConfig config, String clusterXml, FailureDetector fd) {
        for(SystemStoreConstants.SystemStoreName storeName: SystemStoreConstants.SystemStoreName.values()) {
            SystemStore sysStore = new SystemStore(storeName.name(),
                                                   config.getBootstrapUrls(),
                                                   config.getClientZoneId(),
                                                   clusterXml,
                                                   fd);
            this.sysStoreMap.put(storeName.name(), sysStore);
        }
    }

    public SystemStore<String, Long> getVersionStore() {
        String name = SystemStoreConstants.SystemStoreName.voldsys$_metadata_version.name();
        SystemStore<String, Long> sysVersionStore = sysStoreMap.get(name);
        return sysVersionStore;
    }

    public SystemStore<String, String> getClientRegistryStore() {
        String name = SystemStoreConstants.SystemStoreName.voldsys$_client_registry.name();
        SystemStore<String, String> sysRegistryStore = sysStoreMap.get(name);
        return sysRegistryStore;
    }

    public SystemStore<String, String> getMetadataVersionStore() {
        String name = SystemStoreConstants.SystemStoreName.voldsys$_metadata_version_persistence.name();
        SystemStore<String, String> sysVersionStore = sysStoreMap.get(name);
        return sysVersionStore;
    }
}