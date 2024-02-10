package it.gb.sportivo.presentation.dummy_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.gb.sportivo.App
import it.gb.sportivo.R
import it.gb.sportivo.ViewModuleFactory
import it.gb.sportivo.databinding.FragmentResultBinding
import javax.inject.Inject


private const val RIGHT = "right"
private const val WRONG = "wrong"

class ResultFragment : Fragment() {
    private var paramWrong: Int = 0
    private var paramRight: Int = 0

    private var _binding: FragmentResultBinding? = null
    private val binding: FragmentResultBinding
        get() = _binding ?: throw RuntimeException("FragmentResultBinding is null ")


    @Inject
    lateinit var viewModelFactory: ViewModuleFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ResultViewModel::class.java]
    }

    private val daggerApplicationComponent by lazy {
        (requireActivity().application as App).component
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        daggerApplicationComponent.inject(this)

        arguments?.let {
            paramRight = it.getInt(RIGHT)
            paramWrong = it.getInt(WRONG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTryAgain.setOnClickListener {
            val welcomeFragment =
                WelcomeFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_dummy, welcomeFragment).commit()
        }


        binding.correctAnswers.text = String.format(
            getString(R.string.correct_answers), paramRight
        )
        binding.wrongAnswers.text = String.format(
            getString(R.string.wrong_answers), paramWrong
        )
        var points = 0
        if (paramRight > paramWrong)
            points = paramRight - paramWrong

        binding.points.text = String.format(
            getString(R.string.points), points
        )

        viewModel.saveRecord(points)
        val record = viewModel.getRecord()
        binding.bestResult.text = String.format(
            getString(R.string.best_result), record
        )


    }

    companion object {
        @JvmStatic
        fun newInstance(right: Int, wrong: Int) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putInt(RIGHT, right)
                    putInt(WRONG, wrong)
                }
            }
    }
}