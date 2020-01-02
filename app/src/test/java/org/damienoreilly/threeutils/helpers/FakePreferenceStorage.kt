package org.damienoreilly.threeutils.helpers

import org.damienoreilly.threeutils.repository.PreferenceStorage

class FakePreferenceStorage : PreferenceStorage {
    override var alertInternetExpiring: Boolean = false
    override var my3UserName: String? = null
    override var my3Password: String? = null
    override var autoEnterCompetitions: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var threePlusUserName: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var threePlusPassword: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}