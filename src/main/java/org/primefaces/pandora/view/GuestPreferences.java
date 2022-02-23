/*
   Copyright 2009-2021 PrimeTek.

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.primefaces.pandora.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@SessionScoped
public class GuestPreferences implements Serializable {
    
    private String layoutPrimaryColor = "teallight";
    
    private String topbarTheme = "teallight";
    
    private String componentTheme = "bluegrey";
    
    private String menuMode = "layout-slim-plus";
    
    private String menuColor = "light";
    
    private String menuTheme = "teallight";

    private String inputStyle = "outlined";

    private boolean groupedMenu = true;

    private boolean darkLogo;

    private List<ComponentTheme> componentThemes;
    
    private List<TopbarTheme> topbarThemes;
    
    private Map<String, List<MenuTheme>> menuColors;

    private List<Palette> palettes;

    private Palette selectedPalette;

    @PostConstruct
    public void init() {
        componentThemes = new ArrayList<>();
        topbarThemes = new ArrayList<>();
        menuColors = new HashMap<>();
        palettes = new ArrayList<>();

        /************** Palettes ********************/
        
        /* Menu: Light | Active: Blue | Topbar: Blue | Components: Blue */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("blue", "#2E88FF"),
                new Color("blue", "#2E88FF"),
                new Color("blue", "#2E88FF")
            )
        );

         /* Menu: Dark | Active: Blue | Topbar: Blue | Components: Blue-Grey */
         palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("blue", "#2E88FF"),
                new Color("blue", "#2E88FF"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Light | Active: Teal-Light | Topbar: Teal-Light | Components: Teal-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("teallight", "#21B5AD"),
                new Color("teallight", "#21B5AD")
            )
        );

        /* Menu: Dark | Active: Teal-Light | Topbar: Teal-Light | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("teallight", "#21B5AD"),
                new Color("teallight", "#21B5AD"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Dark | Active: Purple | Topbar: Purple | Components: Purple */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("purple", "#636FC0"),
                new Color("purple", "#636FC0"),
                new Color("purple", "#636FC0")
            )
        );

        /* Menu: Light | Active: Orange | Topbar: Purple | Components: Orange */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("orange", "#EC7A51"),
                new Color("purple", "#636FC0"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Dark | Active: Magenta | Topbar: Magenta | Components: Magenta */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("magenta", "#C03995"),
                new Color("magenta", "#C03995"),
                new Color("magenta", "#C03995")
            )
        );

        /* Menu: Light | Active: Cyan | Topbar: Magenta | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5"),
                new Color("magenta", "#C03995"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Dark | Active: Yellow | Topbar: Yellow | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("yellow", "#F3A841"),
                new Color("yellow", "#F3A841"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Light | Active: Yellow | Topbar: Yellow | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("yellow", "#F3A841"),
                new Color("yellow", "#F3A841"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Dark | Active: Green | Topbar: Green | Components: Green */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("green", "#21B567"),
                new Color("green", "#21B567"),
                new Color("green", "#21B567")
            )
        );

        /* Menu: Light | Active: Green | Topbar: Green | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("green", "#21B567"),
                new Color("green", "#21B567"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Dark | Active: Cyan | Topbar: Cyan | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("cyan", "#2199B5"),
                new Color("cyan", "#2199B5"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Light | Active: Cyan | Topbar: Cyan | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5"),
                new Color("cyan", "#2199B5"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Dark | Active: Purple-Light | Topbar: Purple-Light | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("purplelight", "#9754B8"),
                new Color("purplelight", "#9754B8"),
                new Color("purplelight", "#9754B8")
            )
        );

        /* Menu: Light | Active: Purple-Light | Topbar: Purple-Light | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("purplelight", "#9754B8"),
                new Color("purplelight", "#9754B8"),
                new Color("purplelight", "#9754B8")
            )
        );

        /* Menu: Dark | Active: Orange | Topbar: Orange | Components: Orange */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("orange", "#EC7A51"),
                new Color("orange", "#EC7A51"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Light | Active: Orange | Topbar: Orange | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("orange", "#EC7A51"),
                new Color("orange", "#EC7A51"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Dark | Active: Red | Topbar: Red | Components: Red */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("red", "#FE566D"),
                new Color("red", "#FE566D"),
                new Color("red", "#FE566D")
            )
        );

        /* Menu: Light | Active: Red | Topbar: Red | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("red", "#FE566D"),
                new Color("red", "#FE566D"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Dark | Active: Teal-Dark | Topbar: Teal-Dark | Components: Teal-Light */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("tealdark", "#076D72"),
                new Color("tealdark", "#076D72"),
                new Color("teallight", "#21B5AD")
            )
        );

        /* Menu: Light | Active: Teal-Dark | Topbar: Teal-Dark | Components: Teal-Dark */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("tealdark", "#076D72"),
                new Color("tealdark", "#076D72"),
                new Color("tealdark", "#076D72")
            )
        );

        /* Menu: Dark | Active: Purple-Dark | Topbar: Purple-Dark | Components: Purple-Dark */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("purpledark", "#44358F"),
                new Color("purpledark", "#44358F"),
                new Color("purpledark", "#44358F")
            )
        );
        
        /* Menu: Light | Active: Purple-Dark | Topbar: Purple-Dark | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("purpledark", "#44358F"),
                new Color("purpledark", "#44358F"),
                new Color("purplelight", "#9754B8")
            )
        );

        /* Menu: Light | Active: Indigo | Topbar: Indigo | Components: Magenta */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("indigo", "#303498"),
                new Color("indigo", "#303498"),
                new Color("magenta", "#C03995")
            )
        );

        /* Menu: Dark | Active: Indigo | Topbar: Indigo | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("indigo", "#303498"),
                new Color("indigo", "#303498"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Light | Active: Cyan | Topbar: Seagreen | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5"),
                new Color("seagreen", "#354045"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Light | Active: Orange | Topbar: Seagreen | Components: Orange */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("orange", "#EC7A51"),
                new Color("seagreen", "#354045"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Light | Active: Teal-Light | Topbar: Seagreen | Components: Teal-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("seagreen", "#354045"),
                new Color("teallight", "#21B5AD")
            )
        );

        /* Menu: Dark | Active: Blue | Topbar: Dark | Components: Blue */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("blue", "#2E88FF"),
                new Color("dark", "#252729"),
                new Color("blue", "#2E88FF")
            )
        );

        /* Menu: Light | Active: Purple | Topbar: Dark | Components: Purple */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("purple", "#636FC0"),
                new Color("dark", "#252729"),
                new Color("purple", "#636FC0")
            )
        );

        /* Menu: Dark | Active: Green | Topbar: Dark | Components: Green */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("green", "#21B567"),
                new Color("dark", "#252729"),
                new Color("green", "#21B567")
            )
        );

        /* Menu: Light | Active: Cyan | Topbar: Dark | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5"),
                new Color("dark", "#252729"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Dark | Active: Orange | Topbar: Dark | Components: Orange */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("orange", "#EC7A51"),
                new Color("dark", "#252729"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Light | Active: Indigo | Topbar: Dark | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("indigo", "#303498"),
                new Color("dark", "#252729"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Light | Active: Cyan | Topbar: Light | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5"),
                new Color("light", "#f8fafc"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Light | Active: Teal-Light | Topbar: Light | Components: Teal-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD")
            )
        );

        /* Menu: Light | Active: Purple | Topbar: Light | Components: Purple */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("purple", "#636FC0"),
                new Color("light", "#f8fafc"),
                new Color("purple", "#636FC0")
            )
        );

        /* Menu: Light | Active: Cyan | Topbar: Light | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5"),
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Dark | Active: Orange | Topbar: Light | Components: Orange */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("orange", "#EC7A51"),
                new Color("light", "#f8fafc"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Light | Active: Teal-Dark | Topbar: Light | Components: Teal-Dark */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("tealdark", "#076D72"),
                new Color("light", "#f8fafc"),
                new Color("tealdark", "#076D72")
            )
        );

        /* Menu: Dark | Active: Indigo | Topbar: Light | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("indigo", "#303498"),
                new Color("light", "#f8fafc"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Light | Active: Indigo | Topbar: Blue-Dark | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("indigo", "#303498"),
                new Color("bluedark", "#232946"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Light | Active: Magenta | Topbar: Blue-Dark | Components: Magenta */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("magenta", "#C03995"),
                new Color("bluedark", "#232946"),
                new Color("magenta", "#C03995")
            )
        );

        /* Menu: Light | Active: Purple-Light | Topbar: Blue-Dark | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("purplelight", "#9754B8"),
                new Color("bluedark", "#232946"),
                new Color("purplelight", "#9754B8")
            )
        );

        /* Menu: Light | Active: Yellow | Topbar: Grey | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("yellow", "#F3A841"),
                new Color("gray", "#525557"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Light | Active: Teal-Light | Topbar: Grey | Components: Teal-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("gray", "#525557"),
                new Color("teallight", "#21B5AD")
            )
        );

        /* Menu: Light | Active: Magenta | Topbar: Greylight | Components: Magenta */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("magenta", "#21B5AD"),
                new Color("graylight", "#8B8E90"),
                new Color("magenta", "#C03995")
            )
        );

        /* Menu: Light | Active: Magenta | Topbar: Greylight | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("magenta", "#21B5AD"),
                new Color("graylight", "#8B8E90"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Blue-Dark | Active: Blue-Dark | Topbar: Blue-Dark | Components: Purple Light*/
        palettes.add(
            new Palette(
                new Color("colored", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("purple", "#636FC0")
            )
        );

        /* Menu: Blue-Dark | Active: Blue-Dark | Topbar: Blue-Dark | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("colored", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Blue-Dark | Active: Blue-Dark | Topbar: Blue-Dark | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("colored", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("purplelight", "#9754B8")
            )
        );

        /* Menu: Blue-Dark | Active: Blue-Dark | Topbar: Blue-Dark | Components: Orange */
        palettes.add(
            new Palette(
                new Color("colored", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Blue-Dark | Active: Blue-Dark | Topbar: Blue-Dark | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("colored", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("bluedark", "#232946"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Light | Active: Teallight | Topbar: Tealdark | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("tealdark", "#076D72"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Light | Active: Teallight | Topbar: Tealdark | Components: Teal-Light */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("tealdark", "#076D72"),
                new Color("teallight", "#21B5AD")
            )
        );

        /* Menu: Light | Active: Teallight | Topbar: Tealdark | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("tealdark", "#076D72"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Light | Active: Teallight | Topbar: Tealdark | Components: Orange */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("tealdark", "#076D72"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Light | Active: Teallight | Topbar: Tealdark | Components: Teal-Dark */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("teallight", "#21B5AD"),
                new Color("tealdark", "#076D72"),
                new Color("tealdark", "#076D72")
            )
        );

        /* Menu: Light | Active: Blue | Topbar: Dark | Components: Blue */
        palettes.add(
            new Palette(
                new Color("light", "#f8fafc"),
                new Color("blue", "#2E88FF"),
                new Color("dark", "#252729"),
                new Color("blue", "#2E88FF")
            )
        );

        /* Menu: Blue | Active: Blue | Topbar: Light | Components: Blue */
        palettes.add(
            new Palette(
                new Color("colored", "#2E88FF"),
                new Color("blue", "#2E88FF"),
                new Color("light", "#f8fafc"),
                new Color("blue", "#2E88FF")
            )
        );

        /* Menu: Purple | Active: Purple | Topbar: Purple | Components: Purple */
        palettes.add(
            new Palette(
                new Color("colored", "#636FC0"),
                new Color("purple", "#636FC0"),
                new Color("purple", "#636FC0"),
                new Color("purple", "#636FC0")
            )
        );

        /* Menu: Purple | Active: Purple | Topbar: Darkblue | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("colored", "#636FC0"),
                new Color("purple", "#636FC0"),
                new Color("bluedark", "#232946"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Purple | Active: Purple | Topbar: Dark | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("colored", "#636FC0"),
                new Color("purple", "#636FC0"),
                new Color("dark", "#252729"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Magenta | Active: Magenta | Topbar: Magenta | Components: Magenta */
        palettes.add(
            new Palette(
                new Color("colored", "#C03995"),
                new Color("magenta", "#C03995"),
                new Color("magenta", "#C03995"),
                new Color("magenta", "#C03995")
            )
        );

        /* Menu: Magenta | Active: Magenta | Topbar: Dark | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("colored", "#C03995"),
                new Color("magenta", "#C03995"),
                new Color("dark", "#252729"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Magenta | Active: Magenta | Topbar: Light | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("colored", "#C03995"),
                new Color("magenta", "#C03995"),
                new Color("light", "#f8fafc"),
                new Color("purplelight", "#9754B8")
            )
        );

        /* Menu: Cyan | Active: Cyan | Topbar: Cyan | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("colored", "#2199B5"),
                new Color("cyan", "#2199B5"),
                new Color("cyan", "#2199B5"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Cyan | Active: Cyan | Topbar: Light | Components: Cyan */
        palettes.add(
            new Palette(
                new Color("colored", "#2199B5"),
                new Color("cyan", "#2199B5"),
                new Color("light", "#f8fafc"),
                new Color("cyan", "#2199B5")
            )
        );

        /* Menu: Cyan | Active: Cyan | Topbar: Seagreen | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("colored", "#2199B5"),
                new Color("cyan", "#2199B5"),
                new Color("seagreen", "#354045"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Yellow | Active: Yellow | Topbar: Dark | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("colored", "#F3A841"),
                new Color("yellow", "#F3A841"),
                new Color("dark", "#252729"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Yellow | Active: Yellow | Topbar: Seagreen | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("colored", "#F3A841"),
                new Color("yellow", "#F3A841"),
                new Color("seagreen", "#354045"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Green | Active: Green | Topbar: Green | Components: Green */
        palettes.add(
            new Palette(
                new Color("colored", "#21B567"),
                new Color("green", "#21B567"),
                new Color("green", "#21B567"),
                new Color("green", "#21B567")
            )
        );

        /* Menu: Green | Active: Green | Topbar: Dark | Components: Green */
        palettes.add(
            new Palette(
                new Color("colored", "#21B567"),
                new Color("green", "#21B567"),
                new Color("dark", "#252729"),
                new Color("green", "#21B567")
            )
        );

        /* Menu: Green | Active: Green | Topbar: Light | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("colored", "#21B567"),
                new Color("green", "#21B567"),
                new Color("light", "#f8fafc"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Purple-Light | Active: Purple-Light | Topbar: Purple-Light | Components: Purple-Light */
        palettes.add(
            new Palette(
                new Color("colored", "#9754B8"),
                new Color("purplelight", "#9754B8"),
                new Color("purplelight", "#9754B8"),
                new Color("purplelight", "#9754B8")
            )
        );
        
        /* Menu: Purple-Light | Active: Purple-Light | Topbar: Light | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("colored", "#9754B8"),
                new Color("purplelight", "#9754B8"),
                new Color("light", "#f8fafc"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Orange | Active: Orange | Topbar: Dark | Components: Orange */
        palettes.add(
            new Palette(
                new Color("colored", "#EC7A51"),
                new Color("orange", "#EC7A51"),
                new Color("dark", "#252729"),
                new Color("orange", "#EC7A51")
            )
        );

        /* Menu: Orange | Active: Orange | Topbar: Light | Components: Yellow */
        palettes.add(
            new Palette(
                new Color("colored", "#EC7A51"),
                new Color("orange", "#EC7A51"),
                new Color("light", "#f8fafc"),
                new Color("yellow", "#F3A841")
            )
        );

        /* Menu: Red | Active: Red | Topbar: Dark | Components: Red */
        palettes.add(
            new Palette(
                new Color("colored", "#FE566D"),
                new Color("red", "#FE566D"),
                new Color("dark", "#252729"),
                new Color("red", "#FE566D")
            )
        );

        /* Menu: Red | Active: Red | Topbar: Light | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("colored", "#FE566D"),
                new Color("red", "#FE566D"),
                new Color("light", "#f8fafc"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /* Menu: Dark | Active: Teal-Dark | Topbar: Teal-Dark | Components: Teal-Dark */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("tealdark", "#076D72"),
                new Color("tealdark", "#076D72"),
                new Color("tealdark", "#076D72")
            )
        );

        /* Menu: Dark | Active: Teal-Dark | Topbar: Dark | Components: Blue-Grey */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("tealdark", "#076D72"),
                new Color("dark", "#252729"),
                new Color("bluegrey", "#607D8B")
            )
        );

        /*Menu: Dark | Active: Teal-Dark | Topbar: Light | Components: Teal-Dark */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("tealdark", "#076D72"),
                new Color("light", "#f8fafc"),
                new Color("tealdark", "#076D72")
            )
        );

        /* Menu: Dark | Active: Purple-Dark | Topbar: Purple-Dark | Components: Purple-Dark */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("purpledark", "#44358F"),
                new Color("purpledark", "#44358F"),
                new Color("purpledark", "#44358F")
            )
        );

        /* Menu: Dark | Active: Purple-Dark | Topbar: Purple | Components: Purple */
        palettes.add(
            new Palette(
                new Color("dark", "#252729"),
                new Color("purpledark", "#44358F"),
                new Color("purple", "#636FC0"),
                new Color("purple", "#636FC0")
            )
        );

        /* Menu: Indigo | Active: Indigo | Topbar: Indigo | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("colored", "#303498"),
                new Color("indigo", "#303498"),
                new Color("indigo", "#303498"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Indigo | Active: Indigo | Topbar: Darkblue | Components: Indigo */
        palettes.add(
            new Palette(
                new Color("colored", "#303498"),
                new Color("indigo", "#303498"),
                new Color("bluedark", "#232946"),
                new Color("indigo", "#303498")
            )
        );

        /* Menu: Indigo | Active: Indigo | Topbar: Light | Components: Blue */
        palettes.add(
            new Palette(
                new Color("colored", "#303498"),
                new Color("indigo", "#303498"),
                new Color("light", "#f8fafc"),
                new Color("blue", "#2E88FF")
            )
        );

        /************** Light - Dark Menu ********************/
        List<MenuTheme> menuThemes = new ArrayList<>();
        menuThemes.add(new MenuTheme("Teal Light", "teallight", "#21B5AD"));
        menuThemes.add(new MenuTheme("Blue", "blue","#2E88FF"));
        menuThemes.add(new MenuTheme("Purple", "purple","#636FC0"));
        menuThemes.add(new MenuTheme("Magenta", "magenta", "#C03995"));
        menuThemes.add(new MenuTheme("Cyan", "cyan", "#2199B5"));
        menuThemes.add(new MenuTheme("Yellow", "yellow","#F3A841"));
        menuThemes.add(new MenuTheme("Green", "green", "#21B567"));
        menuThemes.add(new MenuTheme("Purple Light", "purplelight","#9754B8"));
        menuThemes.add(new MenuTheme("Orange", "orange","#EC7A51"));
        menuThemes.add(new MenuTheme("Red", "red","#FE566D"));
        menuThemes.add(new MenuTheme("Teal Dark", "tealdark","#076D72"));
        menuThemes.add(new MenuTheme("Purple Dark", "purpledark", "#44358F"));
        menuThemes.add(new MenuTheme("Indigo", "indigo","#303498"));
        menuColors.put("light", menuThemes);
        menuColors.put("dark", menuThemes);
        
        /************** Custom Menu ********************/
        menuThemes = new ArrayList<>();
        menuThemes.add(new MenuTheme("Blue Dark", "bluedark", "#232946"));
        menuThemes.add(new MenuTheme("Teal Light", "teallight", "#21B5AD"));
        menuThemes.add(new MenuTheme("Blue", "blue","#2E88FF"));
        menuThemes.add(new MenuTheme("Purple", "purple","#636FC0"));
        menuThemes.add(new MenuTheme("Magenta", "magenta", "#C03995"));
        menuThemes.add(new MenuTheme("Cyan", "cyan", "#2199B5"));
        menuThemes.add(new MenuTheme("Yellow", "yellow","#F3A841"));
        menuThemes.add(new MenuTheme("Green", "green", "#21B567"));
        menuThemes.add(new MenuTheme("Purple Light", "purplelight","#9754B8"));
        menuThemes.add(new MenuTheme("Orange", "orange","#EC7A51"));
        menuThemes.add(new MenuTheme("Red", "red","#FE566D"));
        menuThemes.add(new MenuTheme("Teal Dark", "tealdark","#076D72"));
        menuThemes.add(new MenuTheme("Purple Dark", "purpledark", "#44358F"));
        menuThemes.add(new MenuTheme("Indigo", "indigo","#303498"));
        menuColors.put("colored", menuThemes);

        topbarThemes.add(new TopbarTheme("Seagreen", "seagreen", "#354045"));
        topbarThemes.add(new TopbarTheme("Dark", "dark", "#252729"));
        topbarThemes.add(new TopbarTheme("Light", "light", "#f8fafc"));
        topbarThemes.add(new TopbarTheme("Blue Dark", "bluedark", "#232946"));
        topbarThemes.add(new TopbarTheme("Blue", "blue", "#2E88FF"));
        topbarThemes.add(new TopbarTheme("Teal Light", "teallight","#21B5AD"));
        topbarThemes.add(new TopbarTheme("Purple", "purple", "#636FC0"));
        topbarThemes.add(new TopbarTheme("Magenta", "magenta","#C03995"));
        topbarThemes.add(new TopbarTheme("Yellow", "yellow", "#F3A841"));
        topbarThemes.add(new TopbarTheme("Green", "green", "#21B567"));
        topbarThemes.add(new TopbarTheme("Cyan", "cyan", "#2199B5"));
        topbarThemes.add(new TopbarTheme("Gray", "gray", "#525557"));
        topbarThemes.add(new TopbarTheme("Gray Light", "graylight", "#8B8E90"));
        topbarThemes.add(new TopbarTheme("Purple Light", "purplelight","#9754B8"));
        topbarThemes.add(new TopbarTheme("Orange", "orange", "#EC7A51"));
        topbarThemes.add(new TopbarTheme("Red", "red", "#FE566D"));
        topbarThemes.add(new TopbarTheme("Teal Dark", "tealdark", "#076D72"));
        topbarThemes.add(new TopbarTheme("Purple Dark", "purpledark", "#44358F"));
        topbarThemes.add(new TopbarTheme("Indigo", "indigo", "#303498"));

        componentThemes.add(new ComponentTheme("Blue Grey", "bluegrey","#607D8B"));
        componentThemes.add(new ComponentTheme("Teal-Light", "teallight","#21B5AD"));
        componentThemes.add(new ComponentTheme("Blue", "blue","#2E88FF"));
        componentThemes.add(new ComponentTheme("Purple", "purple", "#636FC0"));
        componentThemes.add(new ComponentTheme("Magenta", "magenta","#C03995"));
        componentThemes.add(new ComponentTheme("Yellow", "yellow","#F3A841"));
        componentThemes.add(new ComponentTheme("Green", "green","#21B567"));
        componentThemes.add(new ComponentTheme("Cyan", "cyan","#2199B5"));
        componentThemes.add(new ComponentTheme("Purple-Light", "purplelight", "#9754B8"));
        componentThemes.add(new ComponentTheme("Orange", "orange", "#EC7A51"));
        componentThemes.add(new ComponentTheme("Red", "red", "#FE566D"));
        componentThemes.add(new ComponentTheme("Teal-Dark", "tealdark","#076D72"));
        componentThemes.add(new ComponentTheme("Purple-Dark", "purpledark","#44358F"));
        componentThemes.add(new ComponentTheme("Indigo", "indigo", "#303498"));

        selectedPalette = palettes.get(2);
    }
    
    public String getLayoutConfig() {
        StringBuilder sb = new StringBuilder();
        String menuModeClass = this.menuMode.equals("layout-static") ? "layout-static layout-static-active" : this.menuMode;
        String menuThemeClass = "layout-menu-" + (this.menuColor.equals("colored") ? this.menuTheme : this.menuColor);

        sb.append("layout-topbar-").append(this.topbarTheme);
        sb.append(" ").append(menuThemeClass);
        sb.append(" ").append(menuModeClass);

        return sb.toString();
    }

    public void changePalette(Palette palette) {
        this.setMenuColor(palette.menuColor.name);
        this.setMenuTheme(palette.menuTheme.name);
        this.setTopbarTheme(palette.topbarTheme.name);
        this.setComponentTheme(palette.componentTheme.name);
        this.setSelectedPalette(palette);
    }

    public List<Palette> getPalettes() {
        return palettes;
    }
    
    public String getLayout() {
        return "layout-" + this.layoutPrimaryColor;
    }

    public String getLayoutPrimaryColor() {
        return layoutPrimaryColor;
    }

    public void setLayoutPrimaryColor(String layoutPrimaryColor) {
        this.layoutPrimaryColor = layoutPrimaryColor;
    }

    public String getTopbarTheme() {
        return topbarTheme;
    }

    public boolean isDarkLogo() {
        return this.darkLogo;
    }
    
    public void setTopbarTheme(String topbarTheme) {
        this.topbarTheme = topbarTheme;
        this.darkLogo = this.topbarTheme.equals("light");
        this.setSelectedPalette(null);
    }

    public String getComponentTheme() {
        return componentTheme;
    }

    public void setComponentTheme(String componentTheme) {
        this.componentTheme = componentTheme;
        this.setSelectedPalette(null);
    }

    public String getMenuMode() {
        return menuMode;
    }

    public void setMenuMode(String menuMode) {
        this.menuMode = menuMode;
    }

    public String getMenuColor() {
        return menuColor;
    }

    public void setMenuColor(String menuColor) {
        this.menuColor = menuColor;
        this.menuTheme = this.menuColors.get(menuColor).get(0).getFile();
        this.setSelectedPalette(null);
    }

    public String getMenuTheme() {
        return menuTheme;
    }

    public void setMenuTheme(String menuTheme) {
        this.menuTheme = menuTheme;
        this.setSelectedPalette(null);
    }

    public String getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyleClass() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

    public boolean isGroupedMenu() {
        return this.groupedMenu;
    }

    public void setGroupedMenu(boolean value) {
        this.groupedMenu = value;
    }
    
    public List<ComponentTheme> getComponentThemes() {
        return componentThemes;
    }
    
    public List<TopbarTheme> getTopbarThemes() {
        return topbarThemes;
    }
    
    public Map<String, List<MenuTheme>> getMenuColors() {
        return menuColors;
    }

    public Palette getSelectedPalette() {
        return selectedPalette;
    }

    public void setSelectedPalette(Palette selectedPalette) {
        this.selectedPalette = selectedPalette;
    }

    public class Palette {

        Color menuColor;
        Color menuTheme;
        Color topbarTheme;
        Color componentTheme;

        public Palette(Color menuColor, Color menuTheme, Color topbarTheme, Color componentTheme) {
            this.menuColor = menuColor;
            this.menuTheme = menuTheme;
            this.topbarTheme = topbarTheme;
            this.componentTheme = componentTheme;
        }

        public Color getMenuColor() {
            return this.menuColor;
        }

        public Color getMenuTheme() {
            return this.menuTheme;
        }

        public Color getTopbarTheme() {
            return this.topbarTheme;
        }

        public Color getComponentTheme() {
            return this.componentTheme;
        }
    }

    public class Color {

        String name;
        String code;

        public Color(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return this.name;
        }

        public String getCode() {
            return this.code;
        }
    }

    public class MenuTheme {

        String name;
        String file;
        String color;

        public MenuTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }

    public class TopbarTheme {

        String name;
        String file;
        String color;

        public TopbarTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }

    public class ComponentTheme {

        String name;
        String file;
        String color;

        public ComponentTheme(String name, String file, String color) {
            this.name = name;
            this.file = file;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getFile() {
            return this.file;
        }

        public String getColor() {
            return this.color;
        }
    }
}
