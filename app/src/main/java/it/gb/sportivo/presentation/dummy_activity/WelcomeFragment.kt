package it.gb.sportivo.presentation.dummy_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.gb.sportivo.R
import it.gb.sportivo.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {


    var _binding: FragmentWelcomeBinding? = null
    val binding: FragmentWelcomeBinding
        get() = _binding ?: throw RuntimeException("FragmentWelcomeBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonUnderstand.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.container_dummy,
                GameBoldFragment.newInstance()
            ).commit()

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = WelcomeFragment()
    }
}