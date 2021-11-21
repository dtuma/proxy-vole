package com.github.markusbernhardt.proxy;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.github.markusbernhardt.proxy.selector.fixed.FixedProxySelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;
import com.github.markusbernhardt.proxy.util.ProxyException;

class ProxySearchTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxySearchTest.class);

    @Test
    void testAddCustomStrategyFirst() throws URISyntaxException {

        final ProxySearchStrategy customStrategy = new ProxySearchStrategy() {

            @Override
            public ProxySelector getProxySelector() throws ProxyException {
                final ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
                ps.setSelector("custom", new FixedProxySelector("custom@localhost.com", 1234));
                return ps;
            }

            @Override
            public String getName() {
                return "custom";
            }
        };

        ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
        proxySearch.addStrategy(customStrategy, true);

        ProxySelector myProxySelector = proxySearch.getProxySelector();
        Assertions.assertThat(myProxySelector).isNotNull();

        List<Proxy> proxies = myProxySelector.select(new URI("custom://localhost"));
        LOGGER.info("Current proxies: {}", proxies);
        Assertions.assertThat(proxies).isNotEmpty().hasSize(1);
        Assertions
            .assertThat(proxies.get(0).toString()).matches("HTTP @ custom@localhost\\.com(\\/<unresolved>)?:1234");
    }

    @Test
    void testAddCustomStrategyLast() throws URISyntaxException {

        final ProxySearchStrategy customStrategy = new ProxySearchStrategy() {

            @Override
            public ProxySelector getProxySelector() throws ProxyException {
                ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
                ps.setSelector("custom", new FixedProxySelector("custom@localhost.com", 1234));
                return ps;
            }

            @Override
            public String getName() {
                return "custom";
            }
        };

        ProxySearch proxySearch = ProxySearch.getDefaultProxySearch();
        proxySearch.removeStrategy(Strategy.BROWSER);
        proxySearch.addStrategy(customStrategy, false);

        ProxySelector myProxySelector = proxySearch.getProxySelector();
        Assertions.assertThat(myProxySelector).isNotNull();

        List<Proxy> proxies = myProxySelector.select(new URI("custom://localhost"));
        LOGGER.info("Current proxies: {}", proxies);
        Assertions.assertThat(proxies).isNotEmpty();
        Assertions.assertThat(proxies).isNotEmpty().hasSize(1);
        Assertions
            .assertThat(proxies.get(0).toString()).matches("HTTP @ custom@localhost\\.com(\\/<unresolved>)?:1234");
    }
}
