package paige.navic.data.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import paige.navic.data.database.dao.SyncActionDao
import paige.navic.data.database.entities.SyncActionEntity
import paige.navic.data.database.entities.SyncActionType
import paige.navic.data.session.SessionManager

class SyncManager(
	private val syncDao: SyncActionDao = DbContainer.syncActionDao
) {
	private val scope = CoroutineScope(Dispatchers.IO)

	fun enqueueAction(actionType: SyncActionType, itemId: String) {
		scope.launch {
			syncDao.enqueue(SyncActionEntity(actionType = actionType, itemId = itemId))
			processQueue()
		}
	}

	suspend fun processQueue() {
		val actions = syncDao.getPendingActions()

		for (action in actions) {
			try {
				when (action.actionType) {
					SyncActionType.STAR -> SessionManager.api.star(action.itemId)
					SyncActionType.UNSTAR -> SessionManager.api.unstar(action.itemId)
				}

				syncDao.removeAction(action.id)
				println("SyncManager: Successfully synced ${action.actionType} for ${action.itemId}")

			} catch (e: Exception) {
				println("SyncManager: Network failed. Action left in queue. ${e.message}")
				break
			}
		}
	}
}