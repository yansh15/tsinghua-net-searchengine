<template>
  <div id="home" class="home">
    <img id="home-logo" src="../assets/logo.png">
    <el-input id="home-input" size="medium" v-model="query" class="input-with-select">
      <el-button slot="append" icon="el-icon-search" v-on:click="onSearch()" v-bind:disabled="cannotSearch"></el-button>
    </el-input>
    <el-button type="text" size="medium" class="home-tip" v-if="!displayConfig" v-on:click="displayConfig = true">显示高级搜索选项</el-button>
    <el-button type="text" size="medium" class="home-tip" v-if="displayConfig" v-on:click="displayConfig = false">隐藏高级搜索选项</el-button>
    <el-form id="home-config" v-if="displayConfig" size="small" ref="search" label-width="90px">
      <el-form-item label="搜索范围：">
        <el-checkbox label="HTML" v-model="searchHTML"></el-checkbox>
        <el-checkbox label="PDF" v-model="searchPDF"></el-checkbox>
        <el-checkbox label="MSWORD" v-model="searchWORD"></el-checkbox>
      </el-form-item>
      <el-form-item label="域名限制：">
        <el-input v-model="domain" placeholder="www.example.com"></el-input>
      </el-form-item>
      <el-form-item label="屏蔽词：">
        <el-input v-model="not" placeholder="用空格分隔屏蔽词"></el-input>
      </el-form-item>
    </el-form>
    <div id="home-placeholder"></div>
  </div>
</template>

<script>
export default {
  name: 'home',
  data: function () {
    return {
      displayConfig: false,
      query: '',
      searchHTML: true,
      searchPDF: false,
      searchWORD: false,
      domain: '',
      not: ''
    }
  },
  computed: {
    cannotSearch: function () {
      return this.query.length === 0 || (!this.searchHTML && !this.searchPDF && !this.searchWORD)
    }
  },
  methods: {
    onSearch: function () {
      console.log(this.query)
      this.$router.push({
        path: '/result/1',
        query: {
          query: this.query,
          html: this.searchHTML,
          pdf: this.searchPDF,
          word: this.searchWORD,
          domain: this.domain,
          not: this.not
        }
      })
    }
  },
  components: {}
}
</script>

<style lang="scss">
#home {
  flex: 0 0 auto;
  display: flex;
  display: -webkit-flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
#home-logo {
  flex: 0 0 auto;
  height: 120px;
}
#home-input {
  flex: 0 1 auto;
  width: 750px;
}
.home-tip {
  flex: 0 0 auto;
  align-self: center;
  margin: 15px 0 0 0;
  padding: 0;
  color: grey;
}
#home-config {
  flex: 0 0 auto;
  align-self: center;
  width: 500px;
  margin: 0;
  padding: 10px 0 10px 0;
  color: grey;
}
#home-placeholder {
  flex: 0 0 auto;
  align-self: stretch;
  height: 100px;
}
</style>
