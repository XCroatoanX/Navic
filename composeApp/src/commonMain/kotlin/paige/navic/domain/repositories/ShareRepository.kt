package paige.navic.domain.repositories

import dev.zt64.subsonic.api.model.Share
import paige.navic.data.session.SessionManager

class ShareRepository {
	suspend fun getShares(): List<Share> = SessionManager.api.getShares()
}