package it.gb.sportivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.gb.sportivo.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class ViewModuleFactory @Inject constructor(
    private val viewModelsProviders:
    @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelsProviders[modelClass]?.get() as T
    }
}