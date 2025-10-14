# نظام إدارة فندق المارينا - قاعدة البيانات والتطوير

## نظرة عامة

تم إنشاء نظام متكامل لإدارة الفنادق يعتمد على Kotlin باستخدام أحدث تقنيات Android وMaterial Design 3، ويتبع الأنماط المعمارية الحديثة MVVM مع Repository Pattern ويستخدم:


- **Android Architecture Components**: ViewModel, Room, Navigation Component
- **Reactive Programming**: Kotlin Flows وCoroutines
- **ORM & Local Persistence**: Room Database (KSP) مع Type Converters
- **UI Framework**: Material Design 3 
- **Background Tasks**: Work Manager
- **Dependency Management**: Manual DI عبر Application Class وViewModelProvider.Factory

- **Build System**: Gradle 8.3 مع KSP وtargetSdk=34 مدعوم بالكامل لجميع المكونات.