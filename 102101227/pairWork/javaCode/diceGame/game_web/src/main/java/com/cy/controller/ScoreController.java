package com.cy.controller;

import com.cy.util.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/score")
public class ScoreController {
    public static final String URL1 ="http://localhost:9739/app/data/";
    public static final String URL2 ="http://localhost:9739/app/aidata/";


    @PostMapping("/get")
    public Map<String,Object> getScore(@RequestBody Map<String,String> idForm){

        Map<String,Object> map =new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();

        map.put("score1",restTemplate.getForObject(URL1 + idForm.get("id1"), String.class));

        map.put("score2",restTemplate.getForObject(URL1 + idForm.get("id2"), String.class));

        map.put("status",200);

        return map;
    }
    @PostMapping("/select")
    public Map<String,Object> selectDice(@RequestBody Map<String,String> idForm){

        Map<String,Object> map =new HashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        String id1 = idForm.get("id1");
        String id2 = idForm.get("id2");
        String id3 = idForm.get("id21");
        String id4 = idForm.get("id22");
        if("".equals(id1)){
            id1="0";
        }
        String requestUrl=URL2 + id1 +"/"+ ("".equals(id2)?'0':id2)+"/"+
                ("".equals(id3)?'0':id3)+"/"+("".equals(id4)?'0':id4);
        Result result = restTemplate.getForObject(requestUrl, Result.class);

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> difference = new ArrayList<>();

        int idCount = Integer.parseInt(id1);

        while(idCount>0){
            list2.add(idCount%10);
            idCount=idCount/10;
        }

        if (result != null) {
            while(result.getData()>0){
                list1.add(result.getData()%10);
                result.setData(result.getData()/10);
            }
        }
        for (Integer element : list1) {
            boolean found = false;
            for (int i = 0; i < list2.size(); i++) {
                if (element.equals(list2.get(i))) {
                    list2.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                difference.add(element);
            }
        }
        map.put("data",difference);

        map.put("isRage",result.is_rage);

        map.put("rage",result.getRage());

        map.put("status",200);

        return map;
    }
}
