package paige.navic.di

import org.koin.core.context.startKoin
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
	return startKoin {
		includes(config)
		modules(databaseModule, managerModule, repositoryModule, viewModelModule)
	}
}