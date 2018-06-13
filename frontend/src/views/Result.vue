<template>
  <div id="result" class="result">
    <div id="result-nav">
      <router-link to="/">
        <img id="result-logo" src="../assets/small_logo.png">
      </router-link>
      <div id="result-input">
        <el-input size="medium" v-model="query" class="input-with-select">
          <el-button slot="append" icon="el-icon-search" v-on:click="onSearch()" v-bind:disabled="cannotSearch"></el-button>
        </el-input>
      </div>
      <el-button type="text" size="medium" class="result-tip" v-if="!displayConfig" v-on:click="displayConfig = true">显示高级搜索选项</el-button>
      <el-button type="text" size="medium" class="result-tip" v-if="displayConfig" v-on:click="displayConfig = false">隐藏高级搜索选项</el-button>
    </div>
    <el-form id="result-config" v-if="displayConfig" size="small" ref="search" label-width="90px">
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
    <div id="result-items" v-if="totalHits !== 0">
      <p id="result-items-tip">Tsinghua Net 为您找到相关结果约 {{totalHits}} 个</p>
      <item v-for="(document, index) in documents" v-bind:key="index" v-bind:title="document.title" v-bind:url="document.url" v-bind:text="document.content" v-bind:terms="terms"></item>
      <el-pagination id="result-items-page" @current-change="onPageChange" v-bind:current-page.sync="page" v-bind:page-size="10" layout="prev, pager, next, jumper" v-bind:total="totalHits"></el-pagination>
    </div>
  </div>
</template>

<script>
import Item from '../components/Item.vue'
import axios from 'axios'
export default {
  name: 'result',
  data: function () {
    return {
      query: this.$route.query.query,
      displayConfig: false,
      searchHTML: true,
      searchPDF: false,
      searchWORD: false,
      domain: '',
      not: '',
      totalHits: 0,
      documents: [],
      terms: '',
      page: parseInt(this.$route.params.page)
    }
  },
  computed: {
    cannotSearch: function () {
      return this.query.length === 0
    }
  },
  methods: {
    onSearch: function () {
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
    },
    onPageChange: function (p) {
      this.$router.push({
        path: `/result/${this.page}`,
        query: {
          query: this.$route.query.query,
          html: this.$route.query.html,
          pdf: this.$route.query.pdf,
          word: this.$route.query.word,
          domain: this.$route.query.domain,
          not: this.$route.query.not
        }
      })
    }
  },
  watch: {
    '$route': function (to, from) {
      this.page = parseInt(to.params.page)
      this.query = to.query.query
      this.totalHits = 0
      this.document = []
      this.terms = []
      let that = this
      axios.get('/tnapp/search', {
        params: {
          query: to.query.query,
          html: to.query.html,
          pdf: to.query.pdf,
          word: to.query.word,
          domain: to.query.domain,
          not: to.query.not,
          page: to.params.page
        }
      }).then(function (response) {
        that.totalHits = response.data.totalHits
        that.documents = response.data.documents
        that.terms = response.data.terms.map(function (str) {
          return str.split('/')[0]
        }).join(' ')
      }).catch(function (error) {
        console.log(error)
      })
    }
  },
  mounted: function () {
    let that = this
    axios.get('/tnapp/search', {
      params: {
        query: this.$route.query.query,
        html: this.$route.query.html,
        pdf: this.$route.query.pdf,
        word: this.$route.query.word,
        domain: this.$route.query.domain,
        not: this.$route.query.not,
        page: this.$route.params.page
      }
    }).then(function (response) {
      that.totalHits = response.data.totalHits
      that.documents = response.data.documents
      that.terms = response.data.terms.map(function (str) {
        return str.split('/')[0]
      }).join(' ')
    }).catch(function (error) {
      console.log(error)
    })
  },
  components: {
    Item
  }
}
</script>

<style lang="scss">
#result {
  position: absolute;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
#result-nav {
  width: 100%;
  height: 60px;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  margin-top: 15px;
}
#result-logo {
  flex: 0 0 auto;
  height: 50px;
  margin: 0;
  padding: 5px 10px 5px 13px;
}
#result-input {
  flex: 0 0 auto;
  width: 600px;
}
.result-tip {
  flex: 0 0 auto;
  padding: 10px;
}
#result-config {
  width: 500px;
  margin-left: 85px;
}
#result-items {
  margin-left: 100px;
}
#result-items-tip {
  color: grey;
  font-size: 80%;
}
#result-items-page {
  margin: 0;
  padding: 15px 0 15px 0;
}
</style>
