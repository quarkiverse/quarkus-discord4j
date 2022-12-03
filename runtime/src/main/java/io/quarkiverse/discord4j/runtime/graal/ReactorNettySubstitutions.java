package io.quarkiverse.discord4j.runtime.graal;

import static reactor.netty.http.client.HttpClientSecurityUtils.HOSTNAME_VERIFICATION_CONFIGURER;

import java.net.SocketAddress;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.InjectAccessors;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.channel.EventLoop;
import reactor.netty.http.Http2SslContextSpec;
import reactor.netty.internal.shaded.reactor.pool.InstrumentedPool;
import reactor.netty.tcp.SslProvider;
import reactor.netty.tcp.TcpSslContextSpec;

class ReactorNettySubstitutions {
}

// removing static analysis to SSLContext
@TargetClass(className = "reactor.netty.http.client.HttpClientSecure")
final class HttpClientSecureSubstitution {

    @Alias
    @InjectAccessors(DefaultHttpSslProviderAccessor.class)
    static SslProvider DEFAULT_HTTP_SSL_PROVIDER;

    @Alias
    @InjectAccessors(DefaultHttp2SslProviderAccessor.class)
    static SslProvider DEFAULT_HTTP2_SSL_PROVIDER;

    static final class DefaultHttpSslProviderAccessor {
        static SslProvider get() {
            return DefaultHttpSslProviderLazyHolder.DEFAULT_HTTP_SSL_PROVIDER;
        }

        static void set(SslProvider DEFAULT_HTTP_SSL_PROVIDER) {
        }
    }

    static final class DefaultHttpSslProviderLazyHolder {
        static final SslProvider DEFAULT_HTTP_SSL_PROVIDER = SslProvider
                .addHandlerConfigurator(SslProvider.defaultClientProvider(), HOSTNAME_VERIFICATION_CONFIGURER);
    }

    static final class DefaultHttp2SslProviderAccessor {
        static SslProvider get() {
            return DefaultHttp2SslProviderLazyHolder.DEFAULT_HTTP2_SSL_PROVIDER;
        }

        static void set(SslProvider DEFAULT_HTTP2_SSL_PROVIDER) {
        }
    }

    static final class DefaultHttp2SslProviderLazyHolder {
        static final SslProvider DEFAULT_HTTP2_SSL_PROVIDER = SslProvider
                .addHandlerConfigurator(SslProvider.builder()
                        .sslContext(Http2SslContextSpec.forClient())
                        .build(), HOSTNAME_VERIFICATION_CONFIGURER);
    }
}

@TargetClass(className = "reactor.netty.tcp.TcpClientSecure")
final class TcpClientSecureSubstitution {

    @Alias
    @InjectAccessors(DefaultSslProviderAccessor.class)
    static SslProvider DEFAULT_SSL_PROVIDER;

    static final class DefaultSslProviderAccessor {
        static SslProvider get() {
            return DefaultSslProviderLazyHolder.DEFAULT_SSL_PROVIDER;
        }

        static void set(SslProvider DEFAULT_SSL_PROVIDER) {
        }
    }

    static final class DefaultSslProviderLazyHolder {
        static final SslProvider DEFAULT_SSL_PROVIDER = SslProvider.builder().sslContext(TcpSslContextSpec.forClient()).build();
    }
}

// removing micrometer dependency references
@TargetClass(className = "reactor.netty.http.client.MicrometerHttp2ConnectionProviderMeterRegistrar")
final class MicrometerHttp2ConnectionProviderMeterRegistrarSubstitutions {

    @Substitute
    void registerMetrics(String poolName, String id, SocketAddress remoteAddress, InstrumentedPool.PoolMetrics metrics) {
    }
}

@TargetClass(className = "reactor.netty.resources.MicrometerPooledConnectionProviderMeterRegistrar")
final class MicrometerPooledConnectionProviderMeterRegistrarSubstitutions {

    @Substitute
    void registerMetrics(String poolName, String id, SocketAddress remoteAddress, InstrumentedPool.PoolMetrics metrics) {
    }
}

@TargetClass(className = "reactor.netty.transport.ByteBufAllocatorMetrics")
final class ByteBufAllocatorMetricsSubstitutions {

    @Substitute
    void registerMetrics(String allocType, ByteBufAllocatorMetric metrics, ByteBufAllocator alloc) {
    }
}

@TargetClass(className = "reactor.netty.transport.MicrometerEventLoopMeterRegistrar")
final class MicrometerEventLoopMeterRegistrarSubstitutions {

    @Substitute
    void registerMetrics(EventLoop eventLoop) {
    }
}
