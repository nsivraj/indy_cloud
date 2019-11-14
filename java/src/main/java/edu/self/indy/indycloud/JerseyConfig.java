package edu.self.indy.indycloud;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import edu.self.indy.agency.AgencyResource;
import edu.self.indy.agency.IssuerResource;
import edu.self.indy.agency.ProverResource;
import edu.self.indy.agency.VerifierResource;

@ApplicationPath("/api")
@Component
public class JerseyConfig extends ResourceConfig
{
    public JerseyConfig()
    {
        register(MultiPartFeature.class);
        register(WalletResource.class);
        register(AgencyResource.class);
        register(IssuerResource.class);
        register(ProverResource.class);
        register(VerifierResource.class);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
    }
}

