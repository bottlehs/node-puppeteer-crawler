const axios = require("axios");
const cheerio = require("cheerio");
const mariadb = require('mariadb');
const pool = mariadb.createPool({
    host: '127.0.0.1',
    port: 3306,// default: 3306
    database: 'iboss',
    user:'root',
    password: 'root123'
});

let html = "";

async function getHtml(url) {
  try {
    return await axios.get(url);
  } catch (error) {
    console.error(error);
  }
}

const insertExample = async (item) => {
  let conn;
  try {
    console.log(item);
      conn = await pool.getConnection();

      const result = await conn.query('INSERT INTO company(corporate_name, representative, date_of_establishment, enterprise_type, capital, sales, the_number_of_employees, the_representative_call, homepage, business_hours, address, email) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', 
      [item.corporate_name, item.representative, item.date_of_establishment, item.enterprise_type, item.capital, item.sales, item.the_number_of_employees, item.the_representative_call, item.homepage, item.business_hours, item.address, item.email]);
      console.log(result);
      conn.release();
  }
  catch (err) {
      throw err;
  }
  finally {
      if (conn) conn.release();// release pool
  }
}

async function getRec() {
  if (!html) {
    html = await getHtml('https://www.i-boss.co.kr/insiter.php?design_file=6042.php&PB_1496044103=2');
  }

  // console.log(html.data);

  const $ = cheerio.load(html.data);
  let rec = {};
  let items = [];
  $("#dir_sch_list")
    .find(".cell")
    .each(async function(index, elem) {
      // console.log(elem);
      let title = $(elem).find("a").attr("title");
      let comment = $(elem).find(".comment").text();
      let industry = "";
      let date = "";
      let region = "";
      let hash = [];

      let item = {};

      $(elem).find(".cate_lst dd").each(function(cateIndex, cateElem) {
        console.log(cateIndex);
        if ( cateIndex == 0 ) {
          // 업종
          industry = $(cateElem).text();
        } else if ( cateIndex == 1 ) {
          // 설립일
          date = $(cateElem).text();          
        } else if ( cateIndex == 2 ) {          
          // 지역
          region = $(cateElem).text();          
        }
      });

      $(elem).find(".hash span").each(function(hashIndex, hashElem) {
        hash.push($(hashElem).text());
      });
      
      item.corporate_name = title;
      item.representative = "";
      item.date_of_establishment = "";
      item.enterprise_type = "";
      item.capital = "";             
      item.sales = "";                      
      item.the_number_of_employees = "";            
      item.the_representative_call = "";
      item.homepage = "";   
      item.business_hours = "";   
      item.address = "";         
      item.email = 'email';        
      
      items.push(item);
    });


    console.log('=====결과====');
    console.log(items);

    items.forEach(async item => {
      await insertExample(item);
    });

    console.log(rec);
}

getRec();
