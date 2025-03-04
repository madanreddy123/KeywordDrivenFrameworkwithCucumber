package com.Tagspecifications;

import com.Enums.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagBasedSteps {

    public static class tags {

        /**
         * Helper method to get tags based on the BDD Step.
         */
        public static List<Tag> getTagsBasedOnStep(String bddStep) {

            List<Tag> tags = new ArrayList<>();
            String bddstep = bddStep.toLowerCase().trim();


            if (bddstep.contains("click")) {
                tags.add(Tag.A);
                tags.add(Tag.DIV);
                tags.add(Tag.BUTTON);
                tags.add(Tag.H5);

            }


            if (bddstep.contains("check")) {
                tags.add(Tag.INPUT);
                tags.add(Tag.A);
                tags.add(Tag.DIV);
                tags.add(Tag.BUTTON);

            }

            if (bddstep.contains("radiobutton")) {
                tags.add(Tag.INPUT);
                tags.add(Tag.A);
                tags.add(Tag.DIV);
                tags.add(Tag.BUTTON);

            }

            else if(bddstep.contains("enter")){

                tags.add(Tag.INPUT);
                tags.add(Tag.TEXTAREA);
                tags.add(Tag.SPAN);
            }

            else if(bddstep.contains("validate")){

                tags.add(Tag.DIV);
                tags.add(Tag.SPAN);
                tags.add(Tag.A);
            }

            else if(bddstep.contains("select")){

                tags.add(Tag.SELECT);
            }

            else if(bddstep.contains("choose")){

                tags.add(Tag.INPUT);
                tags.add(Tag.A);
                tags.add(Tag.DIV);
                tags.add(Tag.BUTTON);
            }

            return tags;
        }

    }



}
