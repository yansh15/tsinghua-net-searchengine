<template>
  <div class="item">
    <a v-bind:href="url">
      <p class="item-title no-wrap">{{title}}</p>
    </a>
    <p class="item-text" v-html="displayText"></p>
    <p class="item-url no-wrap">{{url}}</p>
  </div>
</template>

<script>
export default {
  name: 'item',
  props: {
    title: String,
    url: String,
    text: String,
    terms: String
  },
  computed: {
    displayText: function () {
      let ts = this.terms.split(' ')
      let text = this.text
      for (let t of ts) {
        // eslint-disable-next-line
        text = text.replace(eval(`/${t}/g`), '<span class="red-text">' + t + '</span>')
      }
      /* if (this.text.length < 130) {
        return this.text
      } else {
        return this.text.substring(0, 130) + '...'
      } */
      return text
    }
  }
}
</script>

<style lang="scss">
.item {
  width: 600px;
  margin: 0;
  padding: 15px 0 0 0;
}
.item-title {
  text-decoration: none;
  width: 600px;
  margin: 0;
  padding: 7px 0 0 0;
}
.item-text {
  font-size: 80%;
  width: 600px;
  margin: 0;
  padding: 7px 0 0 0;
}

.item-url {
  font-size: 80%;
  width: 600px;
  margin: 0;
  padding: 7px 0 0 0;
}
.no-wrap {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.red-text {
  color: red;
}
</style>
