package org.ekstep.dp.domain

import org.ekstep.ep.samza.util.ContentDataCache
import org.ekstep.ep.samza.util.DialCodeDataCache
import org.ekstep.ep.samza.util.UserDataCache


class EventUpdaterFactory(val contentDataCache: ContentDataCache, val userDataCache: UserDataCache, val dialCodeDataCache: DialCodeDataCache) extends AbstractFactory[_] {
  this.contentDataUpdater = new ContentDataUpdater(contentDataCache)
  this.userDataUpdater = new UserDataUpdater(userDataCache)
  this.dialCodeDataUpdater = new DialcodeDataUpdater(dialCodeDataCache)
  this.collectionDataUpdater = new CollectionDataUpdater(contentDataCache)
  private var contentDataUpdater = null
  private var userDataUpdater = null
  private var dialCodeDataUpdater = null
  private var collectionDataUpdater = null

  override def getInstance(`type`: String): IEventUpdater = `type` match {
    case "content-data-updater" =>
      this.contentDataUpdater
    case "user-data-updater" =>
      this.userDataUpdater
    case "dialcode-data-updater" =>
      this.dialCodeDataUpdater
    case "collection-data-updater" =>
      this.collectionDataUpdater
    case _ =>
      null
  }
}
