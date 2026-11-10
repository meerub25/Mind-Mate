package com.mindmate.wellness.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindmate.wellness.R
import com.mindmate.wellness.ai.AiCompanionEngine
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.data.model.ChatMessage
import com.mindmate.wellness.data.model.MessageSender
import com.mindmate.wellness.databinding.FragmentChatBinding
import com.mindmate.wellness.ui.MainActivity
import com.mindmate.wellness.ui.stress.BreathingExerciseActivity
import com.mindmate.wellness.ui.stress.GroundingExerciseActivity

class AiCompanionFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper
    private lateinit var chatAdapter: ChatAdapter
    private val aiEngine = AiCompanionEngine()

    private var activeLanguage: String = "roman_ur"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        setupRecyclerView()
        setupLanguageChips()
        setupSuggestedTopicChips()
        setupSendButton()
        loadChatHistory()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter { action ->
            when (action) {
                "BREATHING" -> {
                    startActivity(Intent(requireContext(), BreathingExerciseActivity::class.java))
                }
                "GROUNDING" -> {
                    startActivity(Intent(requireContext(), GroundingExerciseActivity::class.java))
                }
                "HELPLINES" -> {
                    (activity as? MainActivity)?.selectNavigationTab(R.id.nav_more)
                }
            }
        }

        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupLanguageChips() {
        binding.chipGroupLang.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                activeLanguage = when (checkedIds[0]) {
                    R.id.chipLangEnglish -> "en"
                    R.id.chipLangUrdu -> "ur"
                    else -> "roman_ur"
                }
            }
        }
    }

    private fun setupSuggestedTopicChips() {
        binding.chipTopicExams.setOnClickListener {
            sendMessage("Mujhe semester exams aur CGPA ki bohot zyada tension ho rahi hai.")
        }
        binding.chipTopicCareer.setOnClickListener {
            sendMessage("Degree ke baad job market aur career uncertainty ka darr satata hai.")
        }
        binding.chipTopicFamily.setOnClickListener {
            sendMessage("Ghar walon ki bohot expectations hain aur wo doosron se compare karte hain.")
        }
        binding.chipTopicHostel.setOnClickListener {
            sendMessage("Hostel me ghar se door akela feel hota hai aur homesick hoon.")
        }
        binding.chipTopicBurnout.setOnClickListener {
            sendMessage("Continuous assignments aur quizzes ki wajah se bilkul burnout aur exhausted hoon.")
        }
    }

    private fun setupSendButton() {
        binding.btnSendMessage.setOnClickListener {
            val text = binding.etMessageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.etMessageInput.setText("")
            }
        }
    }

    private fun sendMessage(userText: String) {
        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            content = userText,
            language = activeLanguage
        )
        dbHelper.insertChatMessage(userMsg)
        chatAdapter.addMessage(userMsg)
        binding.rvChatMessages.scrollToPosition(chatAdapter.itemCount - 1)

        // Generate AI Response
        binding.rvChatMessages.postDelayed({
            val aiResult = aiEngine.processUserMessage(userText, activeLanguage)
            val aiMsg = ChatMessage(
                sender = MessageSender.AI,
                content = aiResult.replyText,
                language = activeLanguage,
                detectedEmotion = aiResult.emotion.tag,
                suggestedAction = aiResult.suggestedAction
            )
            dbHelper.insertChatMessage(aiMsg)
            chatAdapter.addMessage(aiMsg)
            binding.rvChatMessages.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }, 350)
    }

    private fun loadChatHistory() {
        val history = dbHelper.getAllChatMessages()
        chatAdapter.submitList(history)
        if (history.isNotEmpty()) {
            binding.rvChatMessages.scrollToPosition(history.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
