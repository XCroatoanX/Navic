package paige.navic

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import paige.navic.di.initKoin

class NavicApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		initKoin {
			androidContext(this@NavicApplication)
			androidLogger()
		}
	}
}