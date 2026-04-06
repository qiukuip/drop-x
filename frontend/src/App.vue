<template>
  <div :class="{ 'dark-mode': isDarkMode }" class="dropx-app">
    <nav class="navbar is-primary">
      <div class="container">
        <div class="navbar-brand">
          <a class="navbar-item">
            <i class="fas fa-cloud-upload-alt fa-lg mr-2"></i>
            <span class="is-size-4 has-text-weight-bold">DropX</span>
          </a>
        </div>
        <div class="navbar-end is-hidden-mobile">
          <div class="navbar-item">
            <div class="buttons">
              <button class="button is-text is-light" @click="showAbout = true">
                <i class="fas fa-info-circle mr-2"></i>关于
              </button>
              <button class="button is-text is-light" @click="showPrivacy = true">
                <i class="fas fa-shield-alt mr-2"></i>隐私说明
              </button>
              <button class="button is-text is-light" @click="toggleDarkMode">
                <i :class="isDarkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="navbar-end is-hidden-tablet">
          <div class="navbar-item">
            <div class="dropdown is-right" :class="{ 'is-active': mobileMenuActive }">
              <div class="dropdown-trigger">
                <button class="button is-text" @click="mobileMenuActive = !mobileMenuActive">
                  <i class="fas fa-bars"></i>
                </button>
              </div>
              <div class="dropdown-menu">
                <div class="dropdown-content">
                  <a class="dropdown-item" @click="showAbout = true; mobileMenuActive = false">关于</a>
                  <a class="dropdown-item" @click="showPrivacy = true; mobileMenuActive = false">隐私说明</a>
                  <a class="dropdown-item" @click="toggleDarkMode">切换模式</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <section class="section main-section">
      <div class="container">
        <div class="tabs is-centered is-medium mb-5">
          <ul>
            <li :class="{ 'is-active': activeTab === 'create' }">
              <a @click="activeTab = 'create'">
                <span class="icon"><i class="fas fa-upload"></i></span>
                <span>存入</span>
              </a>
            </li>
            <li :class="{ 'is-active': activeTab === 'extract' }">
              <a @click="activeTab = 'extract'">
                <span class="icon"><i class="fas fa-download"></i></span>
                <span>提取</span>
              </a>
            </li>
          </ul>
        </div>

        <div class="card main-card">
          <div class="card-content">
            <template v-if="activeTab === 'create'">
              <div class="field">
                <label class="label">内容类型</label>
                <div class="control">
                  <div class="select is-fullwidth is-medium">
                    <select v-model="contentType">
                      <option value="text">文本</option>
                      <option value="file">文件</option>
                    </select>
                  </div>
                </div>
              </div>

              <template v-if="contentType === 'text'">
                <div class="field">
                  <div class="control">
                    <textarea class="textarea" v-model="textContent" placeholder="请输入要暂存的文本内容，不超过 1w 字。" rows="8"></textarea>
                  </div>
                </div>

                <div class="notification is-info is-light mt-3">
                  <p><i class="fas fa-clock mr-2"></i>存入内容仅保留24小时，请及时提取。</p>
                </div>

                <div class="field">
                  <button class="button is-primary is-medium is-fullwidth" @click="submitText" :class="{ 'is-loading': loading }" :disabled="!textContent.trim() || loading">
                    <span class="icon"><i class="fas fa-save"></i></span>
                    <span>存入</span>
                  </button>
                </div>
              </template>

              <template v-else>
                <div class="file-upload-area" :class="{ 'has-name': selectedFile, 'is-dragover': isDragover }"
                  @dragover.prevent="isDragover = true" @dragleave="isDragover = false" @drop.prevent="handleDrop" @click="triggerFileInput">
                  <input type="file" ref="fileInput" @change="handleFileSelect" style="display: none">
                  <div class="upload-content" v-if="!selectedFile">
                    <i class="fas fa-cloud-upload-alt fa-3x mb-3 has-text-primary"></i>
                    <p class="is-size-5 mb-2">点击或拖拽文件到此处上传</p>
                    <p class="is-size-7 has-text-grey">文件大小不能超过 5GB</p>
                  </div>
                  <div class="selected-file" v-else>
                    <i class="fas fa-file fa-2x mr-3 has-text-primary"></i>
                    <div class="file-info">
                      <p class="is-size-6 has-text-weight-semibold">{{ selectedFile.name }}</p>
                      <p class="is-size-7 has-text-grey">{{ formatFileSize(selectedFile.size) }}</p>
                    </div>
                    <button class="delete is-small" @click.stop="removeFile"></button>
                  </div>
                </div>

                <div class="notification is-info is-light mt-3">
                  <p><i class="fas fa-clock mr-2"></i>存入内容仅保留24小时，请及时提取。</p>
                </div>

                <div class="field mt-4">
                  <button class="button is-primary is-medium is-fullwidth" @click="uploadFile" :class="{ 'is-loading': loading }"
                    :disabled="!selectedFile || loading">
                    <span class="icon"><i class="fas fa-upload"></i></span>
                    <span>上传</span>
                  </button>
                </div>
              </template>

            </template>

            <template v-else>
              <div class="field">
                <label class="label">提取码</label>
                <div class="control">
                  <input class="input is-medium" type="text" v-model="extractCode" placeholder="请输入5位提取码" maxlength="5">
                </div>
              </div>
              <div class="field">
                <div class="control">
                  <div class="select is-fullwidth is-medium">
                    <select v-model="extractType">
                      <option value="text">文本</option>
                      <option value="file">文件</option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="notification is-info is-light mt-3">
                  <p><i class="fas fa-clock mr-2"></i>如提示未找到，请检查内容类型。</p>
                </div>

              <div class="field">
                <button class="button is-primary is-medium is-fullwidth" @click="extractContent" :class="{ 'is-loading': loading }" :disabled="extractCode.length !== 5 || loading">
                  <span class="icon"><i class="fas fa-search"></i></span>
                  <span>提取</span>
                </button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </section>

    <footer class="footer">
      <div class="content has-text-centered">
        <p class="is-size-7 has-text-grey">DropX - 临时文本/文件中转站</p>
      </div>
    </footer>

    <div class="modal" :class="{ 'is-active': showModal }">
      <div class="modal-background" @click="closeModal"></div>
      <div class="modal-card">
        <header class="modal-card-head">
          <p class="modal-card-title has-text-centered">
            <i class="fas fa-check-circle has-text-success mr-2"></i>{{ modalTitle }}
          </p>
          <button class="delete" @click="closeModal"></button>
        </header>
        <section class="modal-card-body">
          <div class="has-text-centered">
            <p class="is-size-3 has-text-weight-bold mb-4">{{ extractedCode }}</p>
            <p class="is-size-7 has-text-danger">
              <i class="fas fa-exclamation-triangle mr-1"></i>提取码仅展示一次，请牢记！
            </p>
          </div>
        </section>
        <footer class="modal-card-foot is-justify-content-center">
          <button class="button is-primary" @click="copyCode">
            <span class="icon"><i class="fas fa-copy"></i></span>
            <span>复制提取码</span>
          </button>
        </footer>
      </div>
    </div>

    <div class="modal" :class="{ 'is-active': showAbout }">
      <div class="modal-background" @click="showAbout = false"></div>
      <div class="modal-card">
        <header class="modal-card-head">
          <p class="modal-card-title">关于 DropX</p>
          <button class="delete" @click="showAbout = false"></button>
        </header>
        <section class="modal-card-body">
          <p class="mb-3">
            <strong>DropX</strong>
            是一个临时文件/文本中转站。
          </p>
          <p class="mb-3">
            如有疑问，请发送邮件至 
            <strong>mimetic-lulls.02@icloud.com</strong>
          </p>
        </section>
      </div>
    </div>

    <div class="modal" :class="{ 'is-active': showPrivacy }">
      <div class="modal-background" @click="showPrivacy = false"></div>
      <div class="modal-card">
        <header class="modal-card-head">
          <p class="modal-card-title">隐私说明</p>
          <button class="delete" @click="showPrivacy = false"></button>
        </header>
        <section class="modal-card-body">
          <ul>
            <li>所有文本/文件将在上传后 24 小时自动删除，不会长期存储。</li>
            <li>存入内容后提取码仅展示一次，凭提取码提取文本/文件。</li>
          </ul>
        </section>
      </div>
    </div>

    <div class="modal" :class="{ 'is-active': showTextModal }">
      <div class="modal-background" @click="showTextModal = false"></div>
      <div class="modal-card">
        <header class="modal-card-head">
          <p class="modal-card-title">提取的文本内容</p>
          <button class="delete" @click="showTextModal = false"></button>
        </header>
        <section class="modal-card-body">
          <textarea class="textarea" readonly :value="extractedText" rows="10"></textarea>
        </section>
        <footer class="modal-card-foot is-justify-content-center">
          <button class="button is-primary" @click="copyText">
            <span class="icon"><i class="fas fa-copy"></i></span>
            <span>复制文本</span>
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      activeTab: 'create',
      contentType: 'text',
      extractType: 'text',
      textContent: '',
      extractCode: '',
      selectedFile: null,
      loading: false,
      isDragover: false,
      showModal: false,
      modalTitle: '',
      extractedCode: '',
      showAbout: false,
      showPrivacy: false,
      showTextModal: false,
      extractedText: '',
      mobileMenuActive: false,
      isDarkMode: localStorage.getItem('darkMode') === 'true'
    }
  },
  methods: {
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode;
      localStorage.setItem('darkMode', this.isDarkMode);
    },
    triggerFileInput() { this.$refs.fileInput.click(); },
    handleFileSelect(event) {
      const file = event.target.files[0];
      if (file && file.size <= 5 * 1024 * 1024 * 1024) { this.selectedFile = file; }
      else if (file) { alert('文件大小不能超过5GB'); }
    },
    handleDrop(event) {
      this.isDragover = false;
      const file = event.dataTransfer.files[0];
      if (file && file.size <= 5 * 1024 * 1024 * 1024) { this.selectedFile = file; }
      else if (file) { alert('文件大小不能超过5GB'); }
    },
    removeFile() { this.selectedFile = null; this.$refs.fileInput.value = ''; },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B';
      const k = 1024, sizes = ['B', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    async submitText() {
      if (!this.textContent.trim()) {
        alert('文本内容不能为空');
        return;
      }
      if (this.textContent.length > 10000) {
        alert('文本内容不能超过 1w 字');
        return;
      }
      this.loading = true;
      try {
        const response = await fetch('/api/text', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ content: this.textContent })
        });
        const result = await response.json();
        if (result.code === 0) {
          this.modalTitle = '文本已保存';
          this.extractedCode = result.data;
          this.showModal = true;
          this.textContent = '';
        } else { alert(result.message); }
      } catch (error) { alert('提交失败: ' + error.message); }
      finally { this.loading = false; }
    },
    async uploadFile() {
      if (!this.selectedFile) return;
      this.loading = true;
      try {
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        const response = await fetch('/api/files', { method: 'POST', body: formData });
        const result = await response.json();
        if (result.code === 0) {
          this.modalTitle = '文件已上传';
          this.extractedCode = result.data;
          this.showModal = true;
          this.removeFile();
        } else { alert(result.message); }
      } catch (error) { alert('上传失败: ' + error.message); }
      finally { this.loading = false; }
    },
    async extractContent() {
      if (this.extractCode.length !== 5) return;
      this.loading = true;
      try {
        if (this.extractType === 'text') {
          const response = await fetch('/api/text/' + this.extractCode);
          const result = await response.json();
          if (result.code === 0) { this.extractedText = result.data; this.showTextModal = true; }
          else { alert(result.message); }
        } else { window.location.href = '/api/files/' + this.extractCode; }
      } catch (error) { alert('提取失败: ' + error.message); }
      finally { this.loading = false; this.extractCode = ''; }
    },
    copyCode() {
      navigator.clipboard.writeText(this.extractedCode).then(() => { alert('提取码已复制'); this.closeModal(); });
    },
    copyText() { navigator.clipboard.writeText(this.extractedText).then(() => { alert('文本已复制'); }); },
    closeModal() { this.showModal = false; this.extractedCode = ''; }
  }
}
</script>

<style>
.dropx-app { min-height: 100vh; display: flex; flex-direction: column; }
.main-section { flex: 1; padding-top: 2rem; padding-bottom: 2rem; }
.main-card { max-width: 600px; margin: 0 auto; box-shadow: 0 4px 20px rgba(0,0,0,0.1); border-radius: 10px; }
.file-upload-area { border: 2px dashed #dbdbdb; border-radius: 10px; padding: 3rem 2rem; text-align: center; cursor: pointer; transition: all 0.3s; background: #fafafa; }
.file-upload-area:hover { border-color: #3273dc; background: #f5f5f5; }
.file-upload-area.is-dragover { border-color: #3273dc; background: #e8f0fe; }
.file-upload-area.has-name { padding: 1.5rem 2rem; }
.selected-file { display: flex; align-items: center; justify-content: center; }
.file-info { text-align: left; margin-right: 1rem; }
.dropdown-content { min-width: 120px; }
.footer { padding: 1.5rem; }

.dark-mode { background-color: #1a1a2e; color: #e0e0e0; }
.dark-mode .navbar.is-primary { background-color: #16213e; }
.dark-mode .main-card, .dark-mode .main-card .card-content { background-color: #16213e; box-shadow: 0 4px 20px rgba(0,0,0,0.3); }
.dark-mode .input, .dark-mode .textarea, .dark-mode .select select { background-color: #0f3460; border-color: #3273dc; color: #e0e0e0; }
.dark-mode .input::placeholder, .dark-mode .textarea::placeholder { color: #a0a0a0; }
.dark-mode .file-upload-area { background-color: #0f3460; border-color: #4a4a6a; }
.dark-mode .file-upload-area:hover { border-color: #3273dc; background-color: #1a3a6e; }
.dark-mode .notification.is-info { background-color: #1a3a6e !important; color: #7ab8ff !important; }
.dark-mode .label, .dark-mode .modal-card-title { color: #e0e0e0; }
.dark-mode .modal-card, .dark-mode .modal-card-head, .dark-mode .modal-card-body, .dark-mode .modal-card-foot { background-color: #16213e; }
.dark-mode .footer { background-color: #0f0f23; }

@media screen and (max-width: 768px) {
  .main-card { margin: 0 0.5rem; }
  .file-upload-area { padding: 2rem 1rem; }
  .tabs.is-medium ul li a { padding: 0.5rem 1rem; }
}
</style>
