package edu.self.indy.indycloud;

import edu.self.indy.agency.AgencyResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;

import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
@Component
public class JerseyConfig extends ResourceConfig
{
    public JerseyConfig()
    {
        register(MultiPartFeature.class);
        register(AgencyResource.class);
        register(AuthorResource.class);
        register(ProverResource.class);
        register(VerifierResource.class);
        register(TrusteeResource.class);
        register(EndorserResource.class);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        register(WalletResource.class);
        register(WalletFilter.class);
        register(CORSResponseFilter.class);
    }
}

