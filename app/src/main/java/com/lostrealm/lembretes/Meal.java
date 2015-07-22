/*
 * Lembretes. This software is intended for students from UNICAMP as a simple reminder of the daily meal.
 * Copyright (C) 2013-2015  Edson Duarte (edsonduarte1990@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lostrealm.lembretes;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Meal implements Serializable {

    private String text, title, summary;
    private Calendar date = new GregorianCalendar();

    Meal(Context context, String content) {
        String[] lines = content.split("<[b,B][r,R] />");

        if (lines.length <= 1)
            throw new RuntimeException("Could not create meal.");

        final String preference = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_restaurant_key), context.getString(R.string.pref_restaurant_default));
        final String preferenceArray[] = context.getResources().getStringArray(R.array.pref_restaurant_values);

        assert preference != null;
        if (preference.equals(preferenceArray[0])) {
            title = removeHtml(lines[0]);
            summary = removeHtml(lines[3]).replace("PRATO PRINCIPAL: ", "");
        } else if (preference.equals(preferenceArray[1])) {
            title = removeHtml(lines[1]);
            summary = removeHtml(lines[10]); // TODO not sure about this
        }
//        else {
//            title = lines[1] + lines[3];
//            summary = lines[5];
//            String[] tmp = Html.fromHtml(title).toString() // TODO this looks awful, should be improved.
//                    .replaceAll(" DE ", "/")
//                    .replace("JANEIRO", "01")
//                    .replace("FEVEREIRO", "02")
//                    .replace("MARÇO", "03")
//                    .replace("JULHO", "07")
//                    .replace("AGOSTO", "08")
//                    .replaceAll("[^0-9/]", "")
//                    .split("/");
//            date.set(Integer.parseInt(tmp[2]), Integer.parseInt(tmp[1]) - 1, Integer.parseInt(tmp[0]));
//        }

        int[] tmp = parseDate(title);
        if (title.contains("JANTAR"))
            date.set(tmp[2], tmp[1] - 1, tmp[0], 22, 0);
        else
            date.set(tmp[2], tmp[1] - 1, tmp[0], 15, 0);

        text = "";
        for (String line : lines) text = text.concat(line.trim() + "<br />");
    }

    public Calendar getDate() {
        return this.date;
    }

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public String getSummary() {
        return this.summary;
    }

    @NonNull private String removeHtml(String withHtml) {
        return Html.fromHtml(withHtml).toString();
    }

    private int[] parseDate(String s) {
        String[] tmp = s.replaceAll("[^0-9]", " ").trim().split(" ");
        int[] aux = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            aux[i] = Integer.parseInt(tmp[i]);
        }
        return aux;
    }
}
