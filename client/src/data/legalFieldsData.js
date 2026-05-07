const legalFieldsData = {
  "الجنائي": {
    icon: "⚖️",
    description: "كل ما يتعلق بالمسطرة الجنائية، الجنح، الجنايات، وحقوق الدفاع في القانون المغربي.",
    lawyers: [
      { id: 1, name: "ذ. كريم التازي", experience: "20 سنة", cases: "800+", rating: "4.9", avatar: "ك" },
      { id: 2, name: "ذت. سناء الناصري", experience: "12 سنة", cases: "340+", rating: "4.7", avatar: "س" }
    ],
    topics: [
      { title: "حقوق المتهم أثناء الحراسة النظرية", views: "25K", duration: "8:15" },
      { title: "الفرق بين الجنحة والجناية في المغرب", views: "18K", duration: "6:30" },
      { title: "مسطرة تقديم شكاية لوكيل الملك", views: "30K", duration: "10:00" }
    ],
    prompts: ["الحراسة النظرية", "تقديم شكاية", "إطلاق سراح مؤقت"]
  },
  "العقار": {
    icon: "🏠",
    description: "قوانين التحفيظ العقاري، الملكية المشتركة، النزاعات العقارية وعقود البيع والشراء.",
    lawyers: [
      { id: 1, name: "ذت. نجوى بنسالم", experience: "15 سنة", cases: "430+", rating: "4.8", avatar: "ن" },
      { id: 2, name: "ذ. عادل الفاسي", experience: "18 سنة", cases: "520+", rating: "4.9", avatar: "ع" }
    ],
    topics: [
      { title: "كيفاش تحمي راسك قبل ما تشري بقعة؟", views: "45K", duration: "12:00" },
      { title: "مشاكل السانديك والملكية المشتركة", views: "22K", duration: "7:45" },
      { title: "مسطرة التحفيظ العقاري في المغرب", views: "19K", duration: "9:20" }
    ],
    prompts: ["التحفيظ العقاري", "عقد بيع", "الشفعة"]
  },
  "الأعمال": {
    icon: "🏢",
    description: "قانون الشركات، العقود التجارية، الإفلاس، والمنازعات بين الشركاء.",
    lawyers: [
      { id: 1, name: "ذ. عمر الحسيني", experience: "22 سنة", cases: "1200+", rating: "5.0", avatar: "ع" },
      { id: 2, name: "ذت. سمية أوحمو", experience: "10 سنوات", cases: "290+", rating: "4.6", avatar: "س" }
    ],
    topics: [
      { title: "خطوات تأسيس شركة SARL في المغرب", views: "38K", duration: "15:20" },
      { title: "كيفية صياغة اتفاقية الشركاء", views: "15K", duration: "11:10" },
      { title: "حقوق المسير في الشركة المحدودة", views: "12K", duration: "8:45" }
    ],
    prompts: ["تأسيس شركة", "صعوبات المقاولة", "العلامة التجارية"]
  },
  "الشغل": {
    icon: "💼",
    description: "عقود الشغل، الطرد التعسفي، حوادث الشغل، ومستحقات نهاية الخدمة.",
    lawyers: [
      { id: 1, name: "ذ. يوسف البقالي", experience: "14 سنة", cases: "600+", rating: "4.9", avatar: "ي" },
      { id: 2, name: "ذت. ليلى العلمي", experience: "9 سنوات", cases: "215+", rating: "4.7", avatar: "ل" }
    ],
    topics: [
      { title: "حساب تعويضات الطرد التعسفي", views: "60K", duration: "14:30" },
      { title: "حقوقك في حالة الاستقالة", views: "28K", duration: "9:15" },
      { title: "واجبات المشغل بخصوص CNSS", views: "34K", duration: "12:45" }
    ],
    prompts: ["طرد تعسفي", "عقد CDD", "الراحة الأسبوعية"]
  },
  "الأسرة": {
    icon: "👨‍👩‍👧",
    description: "الزواج، الطلاق، النفقة، الحضانة، والإرث وفق مدونة الأسرة المغربية.",
    lawyers: [
      { id: 1, name: "ذت. فاطمة الزهراء", experience: "16 سنة", cases: "720+", rating: "4.8", avatar: "ف" },
      { id: 2, name: "ذ. حمزة الإدريسي", experience: "11 سنة", cases: "310+", rating: "4.7", avatar: "ح" }
    ],
    topics: [
      { title: "مسطرة طلاق الشقاق في المغرب", views: "90K", duration: "18:00" },
      { title: "كيفاش تطلب النفقة والحضانة؟", views: "55K", duration: "13:40" },
      { title: "تقسيم الإرث: القواعد الأساسية", views: "42K", duration: "20:00" }
    ],
    prompts: ["طلاق الشقاق", "إثبات الزواج", "الحضانة"]
  }
};

export default legalFieldsData;
