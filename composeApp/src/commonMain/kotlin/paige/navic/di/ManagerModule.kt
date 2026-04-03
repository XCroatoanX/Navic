package paige.navic.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import paige.navic.data.database.SyncManager

val managerModule = module {
	single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

	singleOf(::SyncManager)
}
