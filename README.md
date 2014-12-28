FabProgress
===============

Floating Action Button with Progress Ring

![LeftOffset](http://mirukerapps.com/wp/wp-content/uploads/2014/12/GIF_20141228_204743.gif)

Usage
---------------

### Layout XML

```
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools" 
    android:layout_width="match_parent"
    android:layout_height="match_parent" 
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:orientation="vertical"
    android:paddingBottom="@dimen/activity_vertical_margin" tools:context=".MainActivity">

    <com.miruker.fabprogress.Fab
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/fabAction"
        android:padding="50dp"
        android:layout_width="64dp"
        android:layout_height="64dp"
        app:fabColor="#E91E63"
        android:layout_gravity="center"
        app:fabDepth="1"
        app:progressColor="#009688"
        app:progressBackgroundColor="@android:color/white"
        app:fabDrawable="@drawable/ic_arrow_back"
        app:fabShadowRadius="4"
        />

</FrameLayout>
```


### Style

* backbround color : colorPrimary
* indicator color : colorAccent or colorControlActivated

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="Fab">
        <attr name="fabDrawable" format="reference" />
        <attr name="fabColor" format="color" />
        <attr name="progressColor" format="color" />
        <attr name="progressBackgroundColor" format="color" />
        <attr name="fabDepth" format="integer" />
        <attr name="fabShadowRadius" format="float" />
    </declare-styleable>
</resources>
```
LICENSE
---------------

```
Copyright 2014 Katsuki Nakatani

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```