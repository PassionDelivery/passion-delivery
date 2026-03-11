package com.example.pdelivery.shared;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageUtil implements WebMvcConfigurer {

	private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 30, 50);
	private static final int DEFAULT_SIZE = 10;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new PageableSizeResolver());
	}

	static class PageableSizeResolver extends PageableHandlerMethodArgumentResolver {

		@Override
		public Pageable resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

			Pageable pageable = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
			int size = ALLOWED_SIZES.contains(pageable.getPageSize()) ? pageable.getPageSize() : DEFAULT_SIZE;
			return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
		}
	}
}
