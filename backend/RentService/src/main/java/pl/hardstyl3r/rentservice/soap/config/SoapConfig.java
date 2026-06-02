package pl.hardstyl3r.rentservice.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import pl.hardstyl3r.rentservice.security.JwtUtil;
import pl.hardstyl3r.rentservice.soap.JwtSoapInterceptor;

import java.util.List;

@Configuration
@EnableWs
public class SoapConfig extends WsConfigurerAdapter {

    private final JwtUtil jwtUtil;

    public SoapConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean
    public XsdSchema pasSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/pas-rent.xsd"));
    }

    @Bean(name = "pas")
    public DefaultWsdl11Definition pasWsdl(XsdSchema pasSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("PasRentPort");
        definition.setLocationUri("/ws");
        definition.setTargetNamespace("http://p.lodz.pl/pas/soap");
        definition.setSchema(pasSchema);
        return definition;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new JwtSoapInterceptor(jwtUtil));
    }
}
