# Marina Hotel Android Management System - Quick Setup Guide

## Installation & Deployment

### Method 1: Automated GitHub Actions (Recommended)
1. Push code to `main` branch
2. Wait for automated build to complete
3. Download APK from Actions artifacts
4. Install on Android devices

### Method 2: Local Build (Advanced)
```bash
# Clone the repository
git clone [repository-url]
cd marina-hotel-wit-app

# Open in Android Studio
# Click "Run" button to build debug APK
# For release: Build → Generate Signed APK
```

### Method 3: Command Line Build
```bash
# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease
```

## First-Time Setup

### 1. Install APK on Device
- Enable "Unknown Sources" in device settings
- Install the downloaded APK file
- Launch the app

### 2. Initial Configuration
- Create admin account
- Configure hotel information
- Set up room inventory
- Configure payment methods
- Import guest data (optional)

### 3. Staff Training
- User account creation
- Basic operation tutorials
- Common workflows practice
- Error handling procedures

## Migration from PHP System

### Data Export
1. Export guest data from existing system
2. Export room inventory
3. Export booking history
4. Export financial records

### Data Import
- Convert to Room database format
- Import using provided tools
- Validate data integrity
- Test system functionality

## Common Issues & Solutions

### Installation Issues
- **App won't install**: Check Android version compatibility
- **Parse error**: Ensure APK file is properly signed
- **Storage permission denied**: Grant storage permissions in device settings

### Runtime Issues
- **Database error**: Clear app data and restart
- **Login fails**: Check network connectivity
- **Reports not generating**: Ensure sufficient internal storage

## Support Resources

### Documentation
- Full system documentation: [DOCUMENTATION.md](DOCUMENTATION.md)
- GitHub Actions build logs
- Android development guides

### Support Channels
- GitHub Issues for technical problems
- Documentation wiki for guides
- Community forums for peer support
- Professional support options available

### Emergency Contacts
- System administrator: [admin-contact]
- Development team: [dev-contact]
- Technical support: [support-contact]

## Success Metrics

After deployment, monitor:
- Installation success rate
- App performance metrics
- User adoption rates
- Error and crash frequencies
- Financial reporting accuracy
- Guest satisfaction improvements

## Next Steps

1. Deploy system to all hotel devices
2. Train staff on new workflows
3. Monitor usage and gathering feedback
4. Plan for advanced features rollout
5. Consider integration with existing systems

---

**Success Indicator**: Users able to complete basic booking, payment, and reporting tasks within 15 minutes of first launch.