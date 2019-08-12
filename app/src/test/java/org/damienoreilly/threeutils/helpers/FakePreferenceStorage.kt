package org.damienoreilly.threeutils.helpers

import org.damienoreilly.threeutils.repository.PreferenceStorage

class FakePreferenceStorage : PreferenceStorage {
    override var alertInternetExpiring: Boolean = false
    override var my3UserName: String? = null
    override var my3Password: String? = null
}