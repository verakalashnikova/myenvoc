package com.myenvoc.android.inject;

import java.util.Map;

import javax.inject.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.myenvoc.android.dao.dictionary.MyenvocDictionary;
import com.myenvoc.android.dao.dictionary.VocabularyDatabase;
import com.myenvoc.android.service.dictionary.DictionaryService;
import com.myenvoc.android.service.dictionary.ImageService;
import com.myenvoc.android.service.network.NetworkService;
import com.myenvoc.android.service.network.Resource;
import com.myenvoc.android.service.user.UserService;
import com.myenvoc.android.service.vocabulary.MyWordInitialDataLoadService;
import com.myenvoc.android.service.vocabulary.VocabularyService;
import com.myenvoc.android.ui.vocabulary.VocabularyViewStrategyFactory;
import com.myenvoc.commons.AndroidAsyncTaskExecutor;
import com.myenvoc.commons.Configuration;
import com.myenvoc.commons.ExecutorFactory;
import com.myenvoc.commons.ExecutorFactory.ExecutorType;
import com.myenvoc.commons.MyTaskExecutor;
import com.myenvoc.commons.SerialMyTaskExecutor;
import com.myenvoc.commons.ThreadPoolExecutor;
import com.myenvoc.commons.VocabularyUtils;

public class MyenvocRoboguiceModule extends AbstractModule {

	/** Mostly to be able to setup serial executor from tests. */
	public static boolean overrideSerialExecutor;

	@Override
	protected void configure() {
		requestStaticInjection(ContextAware.class);

		bind(Resource.class).annotatedWith(BaseResource.class).toProvider(BaseResourceProvider.class).in(Singleton.class);

		bind(ExecutorFactory.class).toProvider(MyTaskExecutorFactoryProvider.class).in(Singleton.class);

		bind(NetworkService.class).in(Singleton.class);
		requestStaticInjection(NetworkService.class);

		bind(VocabularyDatabase.class).in(Singleton.class);
		bind(MyenvocDictionary.class).in(Singleton.class);

		bind(VocabularyService.class).in(Singleton.class);
		bind(ContextAware.class).in(Singleton.class);
		bind(DictionaryService.class).in(Singleton.class);
		bind(ImageService.class).in(Singleton.class);
		bind(MyWordInitialDataLoadService.class).in(Singleton.class);
		bind(UserService.class).in(Singleton.class);

		bind(VocabularyViewStrategyFactory.class).in(Singleton.class);

		requestStaticInjection(VocabularyUtils.class);
		// // DEBUG
		// RegisteredUser user = new RegisteredUser();
		// user.setLogin("speeddy");
		// user.setPassword("pwd");
		// user.setSecret("7CD11440-E32B-EFAF-AA67-56533A532DD9");
		// user.setFirstName("John");
		// user.setLastName("Smith");
		// user.setEmail("john.smith@gmail.com");
		// user.setNativeLanguage("ru");

		// CurrentUser.setUser(user);

	}

	public static class MyTaskExecutorFactoryProvider implements Provider<ExecutorFactory> {

		@Override
		public ExecutorFactory get() {

			Map<ExecutorType, MyTaskExecutor> config = ImmutableMap.of(ExecutorType.ANDROID, new AndroidAsyncTaskExecutor(),
					ExecutorType.SERIAL, new SerialMyTaskExecutor(), ExecutorType.POOLED, new ThreadPoolExecutor());
			ExecutorFactory executorFactory = new ExecutorFactory(config, overrideSerialExecutor ? ExecutorType.SERIAL : null);
			ExecutorFactory.instance = executorFactory;
			return executorFactory;
		}

	}

	public static class BaseResourceProvider implements Provider<Resource> {
		@Override
		public Resource get() {
			return Resource.forUrl(Configuration.myenvoc_host).path("api");
		}
	}

}