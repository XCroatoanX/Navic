package paige.navic.shared

import platform.Foundation.NSLog

actual object Logger {
	private fun log(tag: String, msg: String, tr: Throwable?) {
		NSLog("%@: %@", tag, msg)
		tr?.printStackTrace()
	}

	actual fun e(tag: String, msg: String, tr: Throwable?) { log(tag, msg, tr) }

	actual fun i(tag: String, msg: String, tr: Throwable?) { log(tag, msg, tr) }

	actual fun w(tag: String, msg: String, tr: Throwable?) { log(tag, msg, tr) }
}