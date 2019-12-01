package org.damienoreilly.threeutils.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.launch
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.damienoreilly.threeutils.repository.ThreePlusRepository
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Error
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Success
import org.damienoreilly.threeutils.worker.My3Worker
import org.damienoreilly.threeutils.worker.ThreePlusWorker
import java.util.concurrent.TimeUnit

class ThreePlusSetupViewModel(
        private val preferenceStorage: PreferenceStorage,
        private val threePlusRepository: ThreePlusRepository,
        private val workManager: WorkManager
) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginError = MutableLiveData<String>()
    val working = MutableLiveData<Boolean>()

    fun validateCredentials() {
        loginError.postValue("")
        if (username.value != null && password.value != null) {
            working.postValue(true)
            viewModelScope.launch {
                try {
                    when (val login = threePlusRepository.login(username.value!!, password.value!!)) {
                        is Success -> saveCredentialsAndSetupWorker(username.value!!, password.value!!)
                        is Error -> loginError.postValue(login.error.toString())
                    }
                } catch (e: Exception) {
                    loginError.postValue("Error connecting to 3Plus.")
                }
                working.postValue(false)
            }

        }

    }

    private fun saveCredentialsAndSetupWorker(username: String, password: String) {
        preferenceStorage.threePlusUserName = username
        preferenceStorage.threePlusPassword = password

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val work = PeriodicWorkRequestBuilder<ThreePlusWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(ThreePlusWorker.THREE_PLUS_WORKER,
                ExistingPeriodicWorkPolicy.KEEP, work)
    }

}