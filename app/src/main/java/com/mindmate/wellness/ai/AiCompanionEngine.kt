package com.mindmate.wellness.ai

import com.mindmate.wellness.data.model.ChatMessage
import com.mindmate.wellness.data.model.MessageSender
import java.util.Locale

/**
 * Intelligent, student-focused AI Emotional Companion.
 * Specially trained for university students in Pakistan facing academic anxiety,
 * CGPA pressure, career uncertainty, and family expectations.
 * Supports multilingual natural dialogue in English, Urdu (اردو), and Roman Urdu.
 */
class AiCompanionEngine {

    enum class DetectedEmotion(val tag: String) {
        ACADEMIC_EXAM_STRESS("Exam & CGPA Anxiety"),
        CAREER_UNCERTAINTY("Career & Future Fear"),
        FAMILY_EXPECTATIONS("Family Expectations"),
        LONELINESS_ISOLATION("Hostel & Loneliness"),
        BURNOUT_EXHAUSTION("Burnout & Fatigue"),
        FINANCIAL_PRESSURE("Financial Tension"),
        CRISIS_ALERT("Urgent Crisis"),
        GENERAL_OVERWHELMED("Overwhelmed"),
        POSITIVE_MOMENTUM("Hopeful")
    }

    data class AiResponse(
        val replyText: String,
        val emotion: DetectedEmotion,
        val suggestedAction: String? = null // e.g., "BREATHING", "GROUNDING", "HELPLINES"
    )

    /**
     * Generates an empathetic, context-aware student response.
     */
    fun processUserMessage(userText: String, forcedLang: String? = null): AiResponse {
        val cleanText = userText.trim().lowercase(Locale.ROOT)
        val detectedLang = forcedLang ?: detectLanguage(cleanText)

        // 1. Critical Safety & Crisis Check
        if (isCrisisKeywords(cleanText)) {
            return generateCrisisResponse(detectedLang)
        }

        // 2. Emotion & Topic Classification
        val emotion = classifyStudentEmotion(cleanText)

        // 3. Multilingual Empathetic Generation
        return when (detectedLang) {
            "ur" -> generateUrduResponse(emotion, cleanText)
            "roman_ur" -> generateRomanUrduResponse(emotion, cleanText)
            else -> generateEnglishResponse(emotion, cleanText)
        }
    }

    private fun detectLanguage(text: String): String {
        // Check for Urdu Arabic script unicode range (\u0600-\u06FF)
        val hasUrduScript = text.any { it in '\u0600'..'\u06FF' }
        if (hasUrduScript) return "ur"

        // Roman Urdu common markers
        val romanUrduWords = setOf(
            "hai", "hain", "mera", "meri", "mujhe", "bohot", "boht", "kya", "karoon", "karein",
            "tension", "gpa", "parhai", "ghar", "walon", "lag", "raha", "rahi", "hoon", "nahi",
            "khatam", "thak", "akela", "akeli", "samajh", "bhai", "yaar", "ab", "kese"
        )
        val words = text.split("\\s+".toRegex())
        val romanMatches = words.count { romanUrduWords.contains(it) }
        return if (romanMatches >= 2) "roman_ur" else "en"
    }

    private fun isCrisisKeywords(text: String): Boolean {
        val crisisTerms = listOf(
            "suicide", "kill myself", "end my life", "want to die",
            "mar jaon", "jaan de doon", "khatam kar doon", "zindagi khatam",
            "خودکشی", "مرنا چاہتا", "زندگی ختم"
        )
        return crisisTerms.any { text.contains(it) }
    }

    private fun classifyStudentEmotion(text: String): DetectedEmotion {
        return when {
            // Exams & CGPA
            text.containsAny("exam", "exams", "paper", "cgpa", "gpa", "midterm", "final", "quiz", "parhai", "marks", "fail", "grades", "assignment") ->
                DetectedEmotion.ACADEMIC_EXAM_STRESS

            // Career & Future
            text.containsAny("job", "career", "future", "degree", "berozgari", "unemployment", "interview", "scope", "kamyabi", "placement") ->
                DetectedEmotion.CAREER_UNCERTAINTY

            // Family Expectations
            text.containsAny("family", "parents", "abbu", "ammi", "ghar wale", "rishta", "comparison", "expectations", "khwahishat", "dabaao") ->
                DetectedEmotion.FAMILY_EXPECTATIONS

            // Hostel & Loneliness
            text.containsAny("hostel", "akela", "alone", "lonely", "homesick", "roommate", "dost nahi", "isolated", "koi nahi") ->
                DetectedEmotion.LONELINESS_ISOLATION

            // Burnout & Exhaustion
            text.containsAny("burnout", "exhausted", "tired", "thak", "no energy", "cant focus", "neend", "sleep", "so nahi pa raha") ->
                DetectedEmotion.BURNOUT_EXHAUSTION

            // Financial
            text.containsAny("fee", "fees", "pese", "money", "financial", "afford", "kharch", "tuition") ->
                DetectedEmotion.FINANCIAL_PRESSURE

            // Positive
            text.containsAny("happy", "khush", "behtar", "better", "passed", "alhamdulillah", "good", "relieved", "achha") ->
                DetectedEmotion.POSITIVE_MOMENTUM

            else -> DetectedEmotion.GENERAL_OVERWHELMED
        }
    }

    private fun generateCrisisResponse(lang: String): AiResponse {
        val text = when (lang) {
            "ur" -> "آپ اکیلے نہیں ہیں۔ آپ کی زندگی بہت قیمتی ہے۔ براہ کرم اس مشکل وقت میں مدد حاصل کریں۔ پاکستان میں مفت کونسلنگ ہیلپ لائن (Umang 0311-7786264) 24 گھنٹے دستیاب ہے۔ براہ کرم فوری رابطہ کریں۔"
            "roman_ur" -> "Aap bilkul akele nahi hain. Aap ki zindagi bohat qeemti hai. Please is waqt kisi se baat karein. Pakistan me Umang Mental Health Helpline (0311-7786264) aur Taskeen (0316-8275336) 24/7 free aur confidential hain. Abhi call karein."
            else -> "I hear your pain, and I want you to know you are not alone. Your life is profoundly valuable. Please connect with immediate professional support. Free 24/7 helplines in Pakistan like Umang (0311-7786264) and Taskeen are available right now to support you with complete confidentiality."
        }
        return AiResponse(text, DetectedEmotion.CRISIS_ALERT, "HELPLINES")
    }

    private fun generateRomanUrduResponse(emotion: DetectedEmotion, text: String): AiResponse {
        return when (emotion) {
            DetectedEmotion.ACADEMIC_EXAM_STRESS -> AiResponse(
                replyText = "Main aap ki pareshani achhi tarah samajh sakta hoon. University me exams aur CGPA ka pressure bohot zyada hota hai. Yaad rakhein ke aap ka CGPA aap ki kabliyat ka aakhri faisla nahi karta. Pehle 2 minute gehri saans lein, aur parhai ko chhotay chhotay 25-minute ke hisson me baantein. Kya aap kisi specific subject ki wajah se pareshan hain?",
                emotion = emotion,
                suggestedAction = "BREATHING"
            )

            DetectedEmotion.CAREER_UNCERTAINTY -> AiResponse(
                replyText = "Career aur future ki fikar har university student ko satati hai, khaas tor par Pakistan ke current job market me. Yeh darr bilkul natural hai. Lekin har cheez ek din me decide nahi hoti. Apni skills develop karne par focus karein, aahista aahista raaste khulte hain. Main aap ke saath hoon, thoda dil halka karein.",
                emotion = emotion
            )

            DetectedEmotion.FAMILY_EXPECTATIONS -> AiResponse(
                replyText = "Hamare maashray me walidain ki umeedon ka bohot bojh hota hai, aur aksar cousins se comparison dil ko takleef pohanchata hai. Lekin aap ki apni ek pehchan hai. Walidain ki fiqar unki muhabbat ki wajah se hoti hai, chahe unka tareeqa sakht lagay. Aap apni koshish jari rakhein, natija Allah par chhor dein.",
                emotion = emotion
            )

            DetectedEmotion.LONELINESS_ISOLATION -> AiResponse(
                replyText = "Hostel ki zindagi me ghar se door rehna aur akelepan ka ehsas bohot mushkil hota hai. Yaad rakhein ke akelapan ek temporary phase hai. Aaj apne kisi purane dost ya ghar walon ko call karke baat karein, ya university me kisi student club ko join karein. Aap akele nahi hain, main yahan sunne ke liye mojood hoon.",
                emotion = emotion
            )

            DetectedEmotion.BURNOUT_EXHAUSTION -> AiResponse(
                replyText = "Lagta hai aap mentally aur physically bohot thak chukay hain. Continuous assignments aur classes se burnout hona aam baat hai. Apne aap ko thoda rest dein. Kuch waqt screen aur kitabon se door reh kar thandi hawa me walk karein ya 2-minute box breathing try karein.",
                emotion = emotion,
                suggestedAction = "BREATHING"
            )

            DetectedEmotion.FINANCIAL_PRESSURE -> AiResponse(
                replyText = "University fees aur personal kharchon ka bojh bohot bhari hota hai. Ziada tar universities me HEC Need-Based Scholarships aur student financial aid offices mojood hote hain. Kya aap ne campus ke financial aid office me apply karne ka socha hai?",
                emotion = emotion
            )

            DetectedEmotion.POSITIVE_MOMENTUM -> AiResponse(
                replyText = "Yeh sun kar bohot khushi hui! Chhoti si kamyabi ya achhi feeling ko zaroor cherish karein. Aap ne bohot mehnat ki hai. Isi positive energy ke saath aage barhein!",
                emotion = emotion
            )

            else -> AiResponse(
                replyText = "Aap jo feel kar rahe hain, wo bilkul valid hai. Dil me baat dabane se bojh barhta hai. Aap bina kisi jhijhak ke yahan apna dil halka kar sakte hain. Aur kya chal raha hai aap ke zehn me?",
                emotion = emotion
            )
        }
    }

    private fun generateUrduResponse(emotion: DetectedEmotion, text: String): AiResponse {
        return when (emotion) {
            DetectedEmotion.ACADEMIC_EXAM_STRESS -> AiResponse(
                replyText = "میں آپ کی پریشانی سمجھ سکتا ہوں۔ امتحانات اور سی جی پی اے کا دباؤ یونیورسٹی میں اکثر بہت بھاری لگتا ہے۔ یاد رکھیں کہ امتحانات کے نمبر آپ کی پوری شخصیت کی عکاسی نہیں کرتے۔ گہری سانس لیں اور اپنے مطالعے کو چھوٹے چھوٹے حصوں میں تقسیم کریں۔",
                emotion = emotion,
                suggestedAction = "BREATHING"
            )

            DetectedEmotion.CAREER_UNCERTAINTY -> AiResponse(
                replyText = "مستقبل اور کیریئر کے بارے میں سوچ کر فکرمند ہونا فطری ہے۔ ہر کامیاب انسان نے کبھی نہ کبھی اسی بے یقینی کا سامنا کیا تھا۔ اپنی صلاحیتوں کو بہتر بنانے پر دھیان دیں، راستے خود بخود بنتے چلے جائیں گے۔",
                emotion = emotion
            )

            DetectedEmotion.FAMILY_EXPECTATIONS -> AiResponse(
                replyText = "خاندان اور والدین کی توقعات کا بوجھ سنبھالنا واقعی مشکل ہوتا ہے۔ لیکن یاد رکھیں کہ آپ کی محنت قیمتی ہے۔ آپ اپنی پوری کوشش کریں اور نتائج کے بارے میں حد سے زیادہ پریشان نہ ہوں۔",
                emotion = emotion
            )

            DetectedEmotion.BURNOUT_EXHAUSTION -> AiResponse(
                replyText = "ایسا لگتا ہے کہ آپ ذہنی طور پر کافی تھک چکے ہیں۔ اپنے آپ کو تھوڑا وقت دیں۔ بریک لینا کمزوری نہیں بلکہ دوبارہ توانائی حاصل کرنے کا ایک ضروری حصہ ہے۔",
                emotion = emotion,
                suggestedAction = "BREATHING"
            )

            else -> AiResponse(
                replyText = "آپ کا جو بھی احساس ہے، وہ قابلِ احترام ہے۔ مائنڈ میٹ آپ کے ساتھ ہے تاکہ آپ بغیر کسی خوف یا جھجک کے اپنے خیالات کا اظہار کر سکیں۔",
                emotion = emotion
            )
        }
    }

    private fun generateEnglishResponse(emotion: DetectedEmotion, text: String): AiResponse {
        return when (emotion) {
            DetectedEmotion.ACADEMIC_EXAM_STRESS -> AiResponse(
                replyText = "I hear you, and it is completely normal to feel exam anxiety and GPA pressure. University in Pakistan can feel like a high-stakes race, but remember: your GPA measures your performance on a specific test, not your ultimate human worth. Take a gentle breath. Let's break down your tasks into small 20-minute chunks.",
                emotion = emotion,
                suggestedAction = "BREATHING"
            )

            DetectedEmotion.CAREER_UNCERTAINTY -> AiResponse(
                replyText = "Career uncertainty is one of the heaviest burdens university students carry. The transition into the professional world feels intimidating, but nobody has their entire life figured out at once. Focus on learning one practical skill today. Every small effort compounds over time.",
                emotion = emotion
            )

            DetectedEmotion.FAMILY_EXPECTATIONS -> AiResponse(
                replyText = "Carrying the weight of family expectations and comparisons can be emotionally exhausting. While parents often want the best for us, their hopes can sometimes feel like an unbearable standard. Remind yourself: you are walking your own path, at your own pace.",
                emotion = emotion
            )

            DetectedEmotion.LONELINESS_ISOLATION -> AiResponse(
                replyText = "Living away from home in hostels or feeling disconnected from campus peers is deeply challenging. Isolation feeds anxiety. Consider reaching out to just one person today, or stepping outside for a brief walk. You matter, and you are not as alone as your thoughts might make you feel.",
                emotion = emotion,
                suggestedAction = "GROUNDING"
            )

            DetectedEmotion.BURNOUT_EXHAUSTION -> AiResponse(
                replyText = "You sound emotionally drained and depleted. Burnout is your mind's way of signaling that your emotional battery is at 1%. Taking rest is not giving up—it is essential maintenance. Can you grant yourself permission to step away from your books and screen for 30 minutes?",
                emotion = emotion,
                suggestedAction = "BREATHING"
            )

            DetectedEmotion.FINANCIAL_PRESSURE -> AiResponse(
                replyText = "Financial strain and tuition concerns create immense mental fatigue while studying. Have you looked into your university's Financial Aid Office or HEC need-based assistance programs? There are often resources available to help support students through difficult semesters.",
                emotion = emotion
            )

            DetectedEmotion.POSITIVE_MOMENTUM -> AiResponse(
                replyText = "That is wonderful to hear! Celebrate these positive moments—they reflect your resilience and hard work. Keep nurturing this healthy momentum!",
                emotion = emotion
            )

            else -> AiResponse(
                replyText = "Thank you for sharing that with me. It takes courage to put personal struggles into words. MindMate is here for you 24/7 without judgment. What feels like the most overwhelming part of this right now?",
                emotion = emotion
            )
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it) }
    }
}
