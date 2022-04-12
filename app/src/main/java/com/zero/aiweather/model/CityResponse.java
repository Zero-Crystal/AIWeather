package com.zero.aiweather.model;

import java.util.List;

public class CityResponse {
    private String name;
    private List<City> city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCity() {
        return city;
    }

    public void setCity(List<City> city) {
        this.city = city;
    }

    public static class City {
        private String name;
        private List<Area> area;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static class Area {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
