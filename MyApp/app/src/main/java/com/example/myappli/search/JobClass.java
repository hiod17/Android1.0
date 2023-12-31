package com.example.myappli.search;

import java.util.ArrayList;

public class JobClass {
    public int job_id;
    public String job_name;
    public String job_area;
    public String salary;
    public String hr_info;
    public String company_name;
    public String company_tag_list;
    public String long_tag_list;
    public String job_url;
    public boolean is_full;
    public boolean is_favor;
    public String job_src;
    public JobClass(int job_id, String job_name, String job_area, String salary,
                    String long_tag_list, String hr_info, String company_name,
                    String company_tag_list, String job_url,boolean is_full,String job_src,boolean is_favor){
        this.job_id = job_id;
        this.company_name = company_name;
        this.company_tag_list = company_tag_list;//job_desc,job_wel
        this.job_url = job_url;
        this.hr_info = hr_info;
        this.job_area = job_area;
        this.job_name = job_name;
        this.salary = salary;
        this.long_tag_list = long_tag_list;//job_need
        this.is_full = is_full;
        this.is_favor=is_favor;
        this.job_src=job_src;
    }
}
