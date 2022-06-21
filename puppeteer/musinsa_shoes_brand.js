const axios = require("axios");
const cheerio = require("cheerio");
const fs = require("fs");

let html = "";
let page = 1;

async function getHtml(url) {
  try {
    return await axios.get(url);
  } catch (error) {
    console.error(error);
  }
}

async function getRec() {
  html = await getHtml(`https://www.musinsa.com/category/005`);

  const $ = cheerio.load(html.data);
  let rec = {};
  let items = [];
  $(".division_attribute .list_division_brand .box-search-brand .brand_unordered_list .brand_list").each(async function(index, elem) {
    let brand = $(elem).attr('data-name');
    console.log(brand);
    const params = {
      name: brand,
      content: brand,
      logo: "",
      cover: ""      
    }
    await axios.post("http://api.onofffit.nextinnovation.kr/api/v1/admin/brand", params, {
      headers: {
        "Content-Type": "application/json",
        "X-AUTH-TOKEN": "Bearer " + "eyJhbGciOiJzaGEyNTYiLCJ0eXAiOiJKV1QifS57InVzZXJfaWQiOjI0OTczNzU1OCwiaWF0IjoxNjQxNzc4OTM3fS5lNWNmYWI2MGEyMDBjMTNmZDA4NTkxZWI1MDI4OWMyYThjYTFiZGNmMjZiNjU3MWFmMzAwYjU2NWEzODYyMGQ3"
      }
    });
  });
}

getRec();