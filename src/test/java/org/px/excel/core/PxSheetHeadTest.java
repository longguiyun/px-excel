package org.px.excel.core;
/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author px
 * @version 1.0.0
 */
public class PxSheetHeadTest {

    @Test
    public void highTest(){

        PxSheetHead root = new PxSheetHead(true);

        PxSheetHead a = new PxSheetHead("a","a");
        PxSheetHead b = new PxSheetHead("b","b");
        PxSheetHead c = new PxSheetHead("c","c");
        PxSheetHead d = new PxSheetHead("d","d");
        PxSheetHead e = new PxSheetHead("b","e");

        root.addNode(null,a);
        root.addNode(null,b);
        root.addNode("a",c);
        root.addNode("a",d);
        root.addNode("b",e);
        System.out.println();


    }
}
