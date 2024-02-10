package it.gb.sportivo.presentation.dummy_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import it.gb.sportivo.R
import it.gb.sportivo.SECONDS_IN_MINUTE
import it.gb.sportivo.data.SelectedItem
import it.gb.sportivo.databinding.FragmentBoldGameBinding
import it.gb.sportivo.presentation.dummy_activity.recycler.BoldMisterAdapter

class GameBoldFragment : Fragment() {


    private var _binding: FragmentBoldGameBinding? = null
    private val binding: FragmentBoldGameBinding
        get() = _binding ?: throw RuntimeException("FragmentBoldGameBinding is null ")


    lateinit var viewModel: GameBoldViewModel

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoldGameBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[GameBoldViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val boldAdapter = BoldMisterAdapter{
            if(it.isSelected!=SelectedItem.NoSelected) return@BoldMisterAdapter
            viewModel.editTigerInfo( it.copy())
        }

        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        binding.recyclerView.adapter = boldAdapter

        viewModel.misterList.observe(viewLifecycleOwner){
            binding.recyclerView.recycledViewPool.clear()
            boldAdapter.submitList(it)
        }

        viewModel.updateMisterQuestion.observe(viewLifecycleOwner){
            binding.questionName.text= it
        }

        viewModel.updateWrongAnswer.observe(viewLifecycleOwner){
            binding.wrongValue.text= it
        }
        viewModel.updateRightAnswer.observe(viewLifecycleOwner){
            binding.rightValue.text= it
        }

        viewModel.updateLeftAnswers.observe(viewLifecycleOwner){
            binding.leftValue.text= it
        }

        viewModel.secondsLeftLD.observe(viewLifecycleOwner) {

            var minutes = 0
            var seconds = it
            if(seconds >= SECONDS_IN_MINUTE)
                minutes = it / SECONDS_IN_MINUTE
            seconds = it - minutes * SECONDS_IN_MINUTE

            binding.clock.text = String.format(
                getString(R.string.clock),
                minutes,
                seconds
            )
        }

        viewModel.finishedGameLD.observe(viewLifecycleOwner){
            if(it) {
                val resultFragment =
                    ResultFragment.newInstance(
                        viewModel.repositoryImpl.rightMisters.size,
                        viewModel.repositoryImpl.wrongMisters.size)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container_dummy, resultFragment).commit()
            }
        }

        if (savedInstanceState == null) {
            viewModel.updateMistersPeriodically()
            viewModel.fetchClock()
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() = GameBoldFragment()
    }
}