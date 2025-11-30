package pl.hardstyl3r.webpas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(new Locale("pl"), new Locale("en"));
    private static final Locale DEFAULT_LOCALE = new Locale("pl");

    @Bean
    public LocaleResolver localeResolver() {
        return new AbstractLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                String langParam = request.getParameter("lang");
                if (langParam != null && !langParam.isEmpty()) {
                    Locale requestedLocale = Locale.forLanguageTag(langParam);
                    if (SUPPORTED_LOCALES.contains(requestedLocale)) {
                        return requestedLocale;
                    }
                }

                String acceptLanguageHeader = request.getHeader("Accept-Language");
                if (acceptLanguageHeader == null || acceptLanguageHeader.isEmpty()) {
                    return DEFAULT_LOCALE;
                }

                List<Locale.LanguageRange> list = Locale.LanguageRange.parse(acceptLanguageHeader);
                Locale locale = Locale.lookup(list, SUPPORTED_LOCALES);

                return locale != null ? locale : DEFAULT_LOCALE;
            }

            @Override
            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
            }
        };
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}