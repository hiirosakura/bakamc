package cn.bakamc.proxy;

import cn.bakamc.common.Bakamc;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = BuildConstants.NAME,
        version = BuildConstants.VERSION,
        authors = {"forpleuvoir"},
        description = BuildConstants.Description
)
public class BakamcProxy implements Bakamc {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public BakamcProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        BakamcProxyInstance.init(this);
        logger.info("Bakamc Proxy is loaded.");
    }

    public ProxyServer getServer() {
        return server;
    }

    @Override
    public @NotNull Logger getLog() {
        return logger;
    }

    @NotNull
    @Override
    public String getBakaName() {
        return BuildConstants.NAME;
    }

    @NotNull
    @Override
    public String getBakaVersion() {
        return BuildConstants.VERSION;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

}
