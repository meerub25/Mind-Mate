require('dotenv').config();
const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// In-memory data store with initial seed data for students
let moodEntries = [
  {
    id: 1,
    score: 4,
    emoji: '🙂',
    label: 'Okay',
    stressLevel: 4,
    triggers: ['Assignments', 'Sleep'],
    notes: 'Busy submission week but feeling balanced.',
    date: new Date().toISOString()
  }
];

let journalEntries = [
  {
    id: 1,
    title: 'Midterm Preparation',
    content: 'Took a walk in the evening after 4 hours of revision. Breathing exercises really helped ground me today.',
    date: new Date().toISOString()
  }
];

let circlePosts = [
  {
    id: 1,
    alias: 'Anonymous Owl 🦉',
    topic: 'Exams',
    content: 'Final year project presentation is tomorrow. The anxiety was high, but taking it one step at a time.',
    meTooCount: 14,
    timestamp: Date.now() - 3600000
  },
  {
    id: 2,
    alias: 'Quiet Pine 🌲',
    topic: 'Hostel',
    content: 'First semester living away from home in hostel. Missing family food today.',
    meTooCount: 21,
    timestamp: Date.now() - 7200000
  }
];

const pakistaniResources = [
  {
    id: 'umang',
    name: 'Umang Pakistan 24/7 Helpline',
    category: 'Helpline',
    phone: '0311-7786264',
    hours: '24/7',
    is24Hours: true,
    isFree: true,
    description: 'First 24/7 free mental health helpline accredited by WHO. Free and confidential support for university students.'
  },
  {
    id: 'rozan',
    name: 'Rozan Counseling Helpline',
    category: 'Helpline',
    phone: '0800-22444',
    hours: 'Mon - Fri: 10 AM to 6 PM',
    is24Hours: false,
    isFree: true,
    description: 'Toll-free psychological counseling for youth addressing family stress, academic strain, and well-being.'
  },
  {
    id: 'taskeen',
    name: 'Taskeen Health Initiative',
    category: 'Helpline',
    phone: '0316-8275336',
    hours: 'Mon - Sat: 9 AM to 11 PM',
    is24Hours: false,
    isFree: true,
    description: 'Free telephonic emotional counseling and stress management guidance across Pakistan.'
  },
  {
    id: 'lums',
    name: 'LUMS Student Counseling Center',
    category: 'On-Campus',
    phone: '042-35608000',
    hours: 'Mon - Fri: 9 AM to 5 PM',
    is24Hours: false,
    isFree: true,
    description: 'Individual counseling, exam stress workshops, and student peer mentoring.'
  },
  {
    id: 'nust',
    name: 'NUST Center for Counseling (C3A)',
    category: 'On-Campus',
    phone: '051-90851571',
    hours: 'Mon - Fri: 9 AM to 5 PM',
    is24Hours: false,
    isFree: true,
    description: 'Confidential student counseling, career guidance, and academic stress support.'
  },
  {
    id: 'emergency',
    name: 'National Emergency Helpline (1122)',
    category: 'Helpline',
    phone: '1122',
    hours: '24/7',
    is24Hours: true,
    isFree: true,
    description: 'Immediate 24/7 nationwide emergency assistance for medical and psychiatric crises.'
  }
];

// Crisis Keywords Safety Check
const CRISIS_KEYWORDS = [
  'suicide', 'kill myself', 'end my life', 'want to die', 'end it all',
  'self harm', 'cutting myself', 'khudkushi', 'khud kushi',
  'marna chahta', 'marna chahti', 'jaan de dunga', 'zindagi khatam'
];

// Health Check
app.get('/api/health', (req, res) => {
  res.json({
    status: 'healthy',
    app: 'MindEase API',
    uptime: process.uptime(),
    timestamp: new Date().toISOString()
  });
});

// Quotes
app.get('/api/quotes/daily', (req, res) => {
  const quotes = [
    { quote: "You don't have to control your thoughts. You just have to stop letting them control you.", author: "Dan Millman" },
    { quote: "Peace comes from within. Do not seek it without.", author: "Buddha" },
    { quote: "Sabr aur yaqeen rakhain. Har mushkil ke baad aasaani aati hai.", author: "MindEase Wisdom" },
    { quote: "Almost everything will work again if you unplug it for a few minutes, including you.", author: "Anne Lamott" },
    { quote: "Your worth is not defined by your CGPA or your grades.", author: "Student Care" }
  ];
  const quote = quotes[Math.floor(Math.random() * quotes.length)];
  res.json(quote);
});

// Resources
app.get('/api/resources', (req, res) => {
  const { category } = req.query;
  if (category) {
    return res.json(pakistaniResources.filter(r => r.category.toLowerCase() === category.toLowerCase()));
  }
  res.json(pakistaniResources);
});

// Moods API
app.get('/api/moods', (req, res) => {
  res.json(moodEntries);
});

app.post('/api/moods', (req, res) => {
  const { score, emoji, label, stressLevel, triggers, notes } = req.body;
  const newEntry = {
    id: Date.now(),
    score: score || 3,
    emoji: emoji || '😐',
    label: label || 'Okay',
    stressLevel: stressLevel || 5,
    triggers: triggers || [],
    notes: notes || '',
    date: new Date().toISOString()
  };
  moodEntries.unshift(newEntry);
  res.status(201).json(newEntry);
});

// Journal API
app.get('/api/journal', (req, res) => {
  res.json(journalEntries);
});

app.post('/api/journal', (req, res) => {
  const { title, content } = req.body;
  if (!content) {
    return res.status(400).json({ error: 'Journal content is required' });
  }
  const newEntry = {
    id: Date.now(),
    title: title || 'Private Reflection',
    content,
    date: new Date().toISOString()
  };
  journalEntries.unshift(newEntry);
  res.status(201).json(newEntry);
});

app.delete('/api/journal/:id', (req, res) => {
  const id = parseInt(req.params.id);
  journalEntries = journalEntries.filter(e => e.id !== id);
  res.json({ message: 'Journal entry removed', id });
});

// Peer Support Circle API
app.get('/api/circle', (req, res) => {
  const { topic } = req.query;
  if (topic && topic !== 'All') {
    return res.json(circlePosts.filter(p => p.topic.toLowerCase() === topic.toLowerCase() || p.content.toLowerCase().includes(topic.toLowerCase())));
  }
  res.json(circlePosts);
});

app.post('/api/circle', (req, res) => {
  const { content, topic, alias } = req.body;
  if (!content) {
    return res.status(400).json({ error: 'Content is required' });
  }

  const contentLower = content.toLowerCase();
  const isCrisis = CRISIS_KEYWORDS.some(kw => contentLower.includes(kw));

  if (isCrisis) {
    return res.status(403).json({
      crisis: true,
      message: 'MindEase detected distress keywords. Immediate helpline support is recommended.',
      helpline: '0311-7786264 (Umang Pakistan 24/7 Helpline)'
    });
  }

  const newPost = {
    id: Date.now(),
    alias: alias || 'Anonymous Student 🌱',
    topic: topic || 'General',
    content,
    meTooCount: 0,
    timestamp: Date.now()
  };
  circlePosts.unshift(newPost);
  res.status(201).json(newPost);
});

app.post('/api/circle/:id/me-too', (req, res) => {
  const id = parseInt(req.params.id);
  const post = circlePosts.find(p => p.id === id);
  if (!post) {
    return res.status(404).json({ error: 'Post not found' });
  }
  post.meTooCount += 1;
  res.json({ id: post.id, meTooCount: post.meTooCount });
});

// AI Emotional Companion API (Multilingual EN, Urdu, Roman Urdu + Crisis Filter)
app.post('/api/chat', (req, res) => {
  const { message } = req.body;
  if (!message) {
    return res.status(400).json({ error: 'Message is required' });
  }

  const lower = message.toLowerCase();
  const isCrisis = CRISIS_KEYWORDS.some(kw => lower.includes(kw));

  if (isCrisis) {
    return res.json({
      reply: 'Aap bilkul akele nahi hain aur aapki zindagi bohot qeemti hai. Please kisi se baat karain. Umang Pakistan 24/7 free helpline par foran rabta karain: 0311-7786264 ya 1122 dial karein. We care deeply about you.',
      crisis: true,
      action: 'CALL_HELPLINE',
      phone: '0311-7786264'
    });
  }

  // Urdu / Roman Urdu detection
  const isRomanUrdu = /kya|hai|nahi|mujhe|bohot|tension|fikar|parhai|kar|raha|rahi|ho|yaar|dost|sab|karo/i.test(lower);

  let replyText = '';
  let actionSuggestion = null;

  if (lower.includes('exam') || lower.includes('cgpa') || lower.includes('paper') || lower.includes('assignment')) {
    if (isRomanUrdu) {
      replyText = 'Exams aur CGPA ki tension samajh aati hai. Ek waqt mein ek topic par focus karein. Thora deep breath lein, aap bohot mehnat kar rahe hain.';
    } else {
      replyText = 'Exam and GPA pressure can feel exhausting. Try breaking your syllabus into small, manageable 25-minute study sprints. How much revision is left today?';
    }
    actionSuggestion = 'TRY_FOCUS_TIMER';
  } else if (lower.includes('alone') || lower.includes('lonely') || lower.includes('hostel') || lower.includes('akela')) {
    if (isRomanUrdu) {
      replyText = 'Hostel life ya parhai mein loneliness mehsoos hona normal hai. Student Support Circle mein doosre students se apni feelings share karke dekhein.';
    } else {
      replyText = 'Hostel life and distance from family can make loneliness feel heavy. You are not alone—many peers share this feeling. Would you like to read thoughts from other students in the Support Circle?';
    }
    actionSuggestion = 'OPEN_SUPPORT_CIRCLE';
  } else if (lower.includes('sleep') || lower.includes('insomnia') || lower.includes('neend') || lower.includes('so nahi')) {
    if (isRomanUrdu) {
      replyText = 'Neend na aana aksar racing thoughts ki wajah se hota hai. Sone se pehle 4-4-4-4 Box Breathing try karein taake dimagh shant ho sake.';
    } else {
      replyText = 'Late-night study habits often disrupt our sleep cycle. Try a gentle 2-minute box breathing session to signal your nervous system that it is safe to rest.';
    }
    actionSuggestion = 'TRY_BOX_BREATHING';
  } else {
    if (isRomanUrdu) {
      replyText = 'Main samajh sakta hoon. Apne dil ki baat share karne ke liye shukriya. Is waqt aap kaisa feel kar rahe hain?';
    } else {
      replyText = 'Thank you for taking this moment to check in with yourself. University life has many ups and downs. What is the main thought on your mind right now?';
    }
  }

  res.json({
    reply: replyText,
    action: actionSuggestion,
    timestamp: Date.now()
  });
});

app.listen(PORT, () => {
  console.log(`[MindEase Backend] Server running on http://localhost:${PORT}`);
});
