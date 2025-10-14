# Marina Hotel - Keystore Setup Guide

## 🚀 إعداد سريع (الطريقة المُوصى بها)

### **للـ Linux/Mac:**
```bash
./setup_keystore.sh
```

### **للـ Windows:**
```cmd
setup_keystore.bat
```

### **البناء بعد الإعداد:**
```bash
# Debug (للتطوير)
./gradlew assembleDebug

# Staging (للاختبار) 
./gradlew assembleStaging

# Release (للإنتاج)
./gradlew assembleRelease
```

---

## 🔧 الإعداد اليدوي

### **1. إنشاء keystore.properties**
```bash
cp keystore.properties.template keystore.properties
```

### **2. تحرير keystore.properties**
```properties
storeFile=release.keystore
storePassword=Marina2024!SecureKey789
keyAlias=marina-hotel-key
keyPassword=HotelApp@2024#Strong456
```

### **3. إنشاء keystore من Base64**
```bash
# Linux/Mac
echo "MIIK+AIBAz..." | base64 -d > release.keystore

# Windows PowerShell
[Convert]::FromBase64String("MIIK+AIBAz...") | Set-Content release.keystore -Encoding Byte
```

---

## 📱 معلومات Keystore

| المعلومة | القيمة |
|---------|--------|
| **Keystore File** | `release.keystore` |
| **Keystore Password** | `Marina2024!SecureKey789` |
| **Key Alias** | `marina-hotel-key` |
| **Key Password** | `HotelApp@2024#Strong456` |

---

## 🛠 للـ CI/CD

### **GitHub Actions**
```yaml
- name: Setup Keystore
  run: |
    echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/release.keystore
  env:
    KEYSTORE_BASE64: ${{ secrets.KEYSTORE_BASE64 }}

- name: Build Release APK
  run: ./gradlew assembleRelease
  env:
    MARINA_KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    MARINA_KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### **GitLab CI**
```yaml
build_release:
  script:
    - echo "$KEYSTORE_BASE64" | base64 -d > app/release.keystore
    - ./gradlew assembleRelease
  variables:
    MARINA_KEYSTORE_PASSWORD: $KEYSTORE_PASSWORD
    MARINA_KEY_PASSWORD: $KEY_PASSWORD
```

---

## ⚠️ تحذيرات أمنية

**❌ لا ترفع للـ Git:**
- `keystore.properties`
- `*.keystore` files
- `local.properties`

**✅ آمن للرفع:**
- `keystore.properties.template`
- `setup_keystore.sh`
- `setup_keystore.bat`

---

## 🔍 التحقق من Keystore

### **معلومات الـ Keystore**
```bash
keytool -list -keystore release.keystore
# Password: Marina2024!SecureKey789
```

### **معلومات الشهادة**
```bash  
keytool -list -alias marina-hotel-key -keystore release.keystore -v
```

### **التحقق من APK المُوقع**
```bash
jarsigner -verify -verbose -certs app-release.apk
```

---

## 🆘 استكشاف الأخطاء

### **خطأ: Keystore not found**
```bash
# تأكد من وجود release.keystore في مجلد app/
ls -la app/release.keystore

# أو قم بتشغيل script الإعداد
./setup_keystore.sh
```

### **خطأ: Wrong password**
```bash
# تأكد من صحة كلمات المرور في keystore.properties
cat keystore.properties
```

### **خطأ: Certificate expired**
```bash
# تحقق من صلاحية الشهادة
keytool -list -alias marina-hotel-key -keystore release.keystore -v
```

---

## 📞 الدعم

للمساعدة في المشاكل:

1. **تحقق من logs البناء**:
   ```bash
   ./gradlew assembleRelease --info --stacktrace
   ```

2. **تحقق من keystore**:
   ```bash
   keytool -list -keystore release.keystore
   ```

3. **تحقق من permissions**:
   ```bash
   ls -la *.keystore keystore.properties
   ```

---

**🏨 Marina Hotel Keystore - Configured & Ready for Production** ✅