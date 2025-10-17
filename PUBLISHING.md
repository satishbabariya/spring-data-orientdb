# Publishing Guide

This guide explains how to publish Spring Data OrientDB to GitHub and distribute it to users.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [GitHub Repository Setup](#github-repository-setup)
- [Publishing to GitHub](#publishing-to-github)
- [Creating a Release](#creating-a-release)
- [GitHub Packages](#github-packages)
- [Maven Central (Optional)](#maven-central-optional)
- [Troubleshooting](#troubleshooting)

## ✅ Prerequisites

- [x] Git repository initialized
- [x] All code committed
- [x] Tests passing locally
- [x] Documentation complete
- [x] GitHub account created
- [x] Repository access configured

## 🚀 GitHub Repository Setup

### 1. Create GitHub Repository

If you haven't already created the repository on GitHub:

1. Go to https://github.com/new
2. Repository name: `spring-data-orientdb`
3. Description: "Spring Data module for OrientDB - Community Edition"
4. Visibility: Public
5. Do NOT initialize with README (we already have one)
6. Click "Create repository"

### 2. Configure Git Remote

The repository is already configured with the correct remote:

```bash
# Verify remote
git remote -v

# Should show:
# origin  https://github.com/satishbabariya/spring-data-orientdb.git (fetch)
# origin  https://github.com/satishbabariya/spring-data-orientdb.git (push)
```

If you need to change it:

```bash
git remote set-url origin https://github.com/satishbabariya/spring-data-orientdb.git
```

## 📤 Publishing to GitHub

### Push to GitHub

```bash
# Push main branch
git push -u origin main

# Push all tags (if any)
git push origin --tags
```

### Verify GitHub Actions

After pushing, GitHub Actions will automatically:
1. Run CI tests on multiple platforms
2. Check code coverage
3. Verify build on Java 17 and 21

Check the Actions tab on GitHub: https://github.com/satishbabariya/spring-data-orientdb/actions

## 🏷️ Creating a Release

### Version Strategy

Follow [Semantic Versioning](https://semver.org/):
- **v0.0.1-SNAPSHOT**: Initial development
- **v0.1.0**: First beta release
- **v1.0.0**: First stable release

### Release Process

#### Option 1: Automated Release (Recommended)

1. **Update version in pom.xml**:
   ```bash
   mvn versions:set -DnewVersion=0.1.0 -DgenerateBackupPoms=false
   ```

2. **Commit version change**:
   ```bash
   git add pom.xml
   git commit -m "chore: bump version to 0.1.0"
   git push origin main
   ```

3. **Create and push tag**:
   ```bash
   git tag -a v0.1.0 -m "Release version 0.1.0"
   git push origin v0.1.0
   ```

4. **GitHub Actions will automatically**:
   - Build the project
   - Run all tests
   - Create GitHub release with changelog
   - Upload JAR artifacts
   - Publish to GitHub Packages

#### Option 2: Manual Release

1. Go to https://github.com/satishbabariya/spring-data-orientdb/releases
2. Click "Draft a new release"
3. Tag version: `v0.1.0`
4. Release title: `Release 0.1.0`
5. Describe the changes
6. Upload built artifacts (JAR files from `target/`)
7. Click "Publish release"

### Release Checklist

Before creating a release:

- [ ] All tests pass: `mvn clean verify`
- [ ] Code coverage meets threshold: `mvn jacoco:check`
- [ ] Documentation is up to date
- [ ] CHANGELOG.md is updated
- [ ] Version number is bumped appropriately
- [ ] No uncommitted changes
- [ ] Branch is up to date with main

## 📦 GitHub Packages

### What is GitHub Packages?

GitHub Packages is GitHub's package hosting service. Your releases are automatically published there.

### Using Published Package

Users can consume the package by adding to their `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-orientdb</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/satishbabariya/spring-data-orientdb</url>
    </repository>
</repositories>
```

Users need to authenticate with GitHub:

**~/.m2/settings.xml**:
```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>GITHUB_USERNAME</username>
            <password>GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

### Verify Package

After release, verify the package:
1. Go to https://github.com/satishbabariya?tab=packages
2. Find `spring-data-orientdb`
3. Check version and files

## 🌐 Maven Central (Optional)

To publish to Maven Central (wider distribution):

### Prerequisites

1. **Sonatype Account**:
   - Create account: https://issues.sonatype.org/
   - Request namespace: `io.github.satishbabariya`

2. **GPG Key**:
   ```bash
   # Generate key
   gpg --gen-key
   
   # List keys
   gpg --list-keys
   
   # Export public key
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
   ```

3. **Update pom.xml**:
   ```xml
   <groupId>io.github.satishbabariya</groupId>
   <artifactId>spring-data-orientdb</artifactId>
   ```

### Publishing to Maven Central

1. **Add Maven Central profile to pom.xml**:
   ```xml
   <profiles>
       <profile>
           <id>release</id>
           <build>
               <plugins>
                   <plugin>
                       <groupId>org.apache.maven.plugins</groupId>
                       <artifactId>maven-gpg-plugin</artifactId>
                       <version>3.1.0</version>
                       <executions>
                           <execution>
                               <id>sign-artifacts</id>
                               <phase>verify</phase>
                               <goals>
                                   <goal>sign</goal>
                               </goals>
                           </execution>
                       </executions>
                   </plugin>
                   <plugin>
                       <groupId>org.sonatype.plugins</groupId>
                       <artifactId>nexus-staging-maven-plugin</artifactId>
                       <version>1.6.13</version>
                       <extensions>true</extensions>
                       <configuration>
                           <serverId>ossrh</serverId>
                           <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
                           <autoReleaseAfterClose>true</autoReleaseAfterClose>
                       </configuration>
                   </plugin>
               </plugins>
           </build>
       </profile>
   </profiles>
   
   <distributionManagement>
       <repository>
           <id>ossrh</id>
           <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
       </repository>
       <snapshotRepository>
           <id>ossrh</id>
           <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
       </snapshotRepository>
   </distributionManagement>
   ```

2. **Configure secrets** in GitHub Actions:
   - `OSSRH_USERNAME`
   - `OSSRH_TOKEN`
   - `GPG_PRIVATE_KEY`
   - `GPG_PASSPHRASE`

3. **Enable Maven Central workflow** in `.github/workflows/release.yml`
   (uncomment the `publish-maven-central` job)

4. **Deploy**:
   ```bash
   mvn clean deploy -P release
   ```

## 📊 Monitoring

### GitHub Insights

Monitor project health:
- **Insights**: https://github.com/satishbabariya/spring-data-orientdb/pulse
- **Traffic**: Views, clones, and visitor traffic
- **Contributors**: Track contributions
- **Community**: Issues, PRs, discussions

### GitHub Actions

Monitor CI/CD:
- **Actions**: https://github.com/satishbabariya/spring-data-orientdb/actions
- **Workflows**: View all workflow runs
- **Badges**: Add status badges to README

### Package Statistics

Track downloads:
- **Packages**: https://github.com/satishbabariya?tab=packages
- **Download stats**: View package usage
- **Versions**: Track version adoption

## 🔧 Troubleshooting

### Push Rejected

```bash
# Error: failed to push some refs
git pull --rebase origin main
git push origin main
```

### Authentication Failed

```bash
# Use personal access token
git remote set-url origin https://YOUR_TOKEN@github.com/satishbabariya/spring-data-orientdb.git

# Or use SSH
git remote set-url origin git@github.com:satishbabariya/spring-data-orientdb.git
```

### GitHub Actions Failing

1. Check workflow logs in Actions tab
2. Run tests locally: `mvn clean verify`
3. Check Java version compatibility
4. Verify dependencies are accessible

### Release Failed

1. Check if tag already exists: `git tag -l`
2. Delete and recreate tag if needed:
   ```bash
   git tag -d v0.1.0
   git push origin :refs/tags/v0.1.0
   git tag -a v0.1.0 -m "Release version 0.1.0"
   git push origin v0.1.0
   ```

### Package Publishing Failed

1. Check GitHub token permissions
2. Verify distribution management in pom.xml
3. Check server configuration in settings.xml

## 📚 Post-Release Tasks

After publishing a release:

1. **Update README badges**:
   ```markdown
   [![GitHub release](https://img.shields.io/github/v/release/satishbabariya/spring-data-orientdb.svg)](https://github.com/satishbabariya/spring-data-orientdb/releases)
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.satishbabariya/spring-data-orientdb.svg)](https://search.maven.org/artifact/io.github.satishbabariya/spring-data-orientdb)
   ```

2. **Announce the release**:
   - Create GitHub discussion
   - Post on social media
   - Update documentation site
   - Notify users in discussions

3. **Monitor feedback**:
   - Watch for issues
   - Respond to questions
   - Track adoption

4. **Plan next release**:
   - Create milestone for next version
   - Prioritize issues and features
   - Update roadmap

## 🔐 Security

### Protecting Credentials

Never commit sensitive information:
- GitHub tokens
- GPG keys
- Maven credentials
- Database passwords

Use GitHub Secrets for:
- `GITHUB_TOKEN` (automatic)
- `OSSRH_USERNAME`
- `OSSRH_TOKEN`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

### Branch Protection

Enable branch protection for `main`:
1. Go to Settings → Branches
2. Add rule for `main`
3. Enable:
   - Require pull request reviews
   - Require status checks to pass
   - Require branches to be up to date
   - Include administrators

## 📞 Getting Help

- **GitHub Discussions**: https://github.com/satishbabariya/spring-data-orientdb/discussions
- **Issues**: https://github.com/satishbabariya/spring-data-orientdb/issues
- **Documentation**: See README.md and CONTRIBUTING.md

## ✅ Quick Reference

```bash
# Check status
git status
mvn clean verify

# Create release
mvn versions:set -DnewVersion=0.1.0 -DgenerateBackupPoms=false
git add pom.xml
git commit -m "chore: bump version to 0.1.0"
git push origin main
git tag -a v0.1.0 -m "Release version 0.1.0"
git push origin v0.1.0

# Manual publish to GitHub Packages
mvn clean deploy

# View logs
git log --oneline
mvn -X clean install  # Debug mode
```

---

**Ready to publish?** Follow the steps above and your project will be live on GitHub! 🚀

