/*
 * 
 *   Copyright © 2019 Eduardo Vindas Cordoba. All rights reserved.
 *  
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 * 
 */
package com.aeongames.expediente.TestDataCreator;

import com.aeongames.expediente.Person;
import com.aeongames.expediente.SerializablePersona;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Eduardo <cartman@aeongames.com>
 */
public class PersonCreator {

    public static final ArrayList<Person> getandsafe( ArrayList<Person> persons) throws IOException {
        List<SerializablePersona> topersist = persons.stream()
                .map(pertoconvert -> {
                    try {
                        return SerializablePersona.toSerializablePersona(pertoconvert);
                    } catch (CertificateEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = gson.toJson(topersist);

        System.out.println(json);
        JsonObject root = new JsonObject();
        root.add("List",gson.toJsonTree(topersist));
        JsonWriter writer=gson.newJsonWriter(new FileWriter("testuser.json"));
        System.out.println(gson.toJson(root.toString()));
        gson.toJson(root, writer);
        writer.flush();
        return persons;
    }

    public static final ArrayList<Person> LoadUsers() throws IOException, CertificateException {
        SerializablePersona[]persons=null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        InputStream resource = PersonCreator.class.getResourceAsStream("/com/aeongames/expediente/resources/testuser.json");
        System.out.println(resource);
        JsonReader reader = gson.newJsonReader(new InputStreamReader(resource));
        JsonObject root = gson.fromJson(reader, JsonObject.class);
        JsonElement json = root.get("List");
        if (!json.isJsonNull()) {
            persons = gson.fromJson(json, SerializablePersona[].class);
        }
        ArrayList<Person> tmp=new ArrayList<>();
        if(persons!=null){
        for (SerializablePersona p:persons) {
            tmp.add(p.convertToPersonClass()) ;
            System.out.println(p);
        }}
        
        return tmp.isEmpty()?null:tmp;
    }

    public static final ArrayList<Person> LoadUsers(String Path) throws IOException, CertificateException {
        SerializablePersona[]persons=null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        InputStream resource = new FileInputStream(Path);
        System.out.println(resource);
        JsonReader reader = gson.newJsonReader(new InputStreamReader(resource));
        JsonObject root = gson.fromJson(reader, JsonObject.class);
        JsonElement json = root.get("List");
        if (!json.isJsonNull()) {
            persons = gson.fromJson(json, SerializablePersona[].class);
        }
        ArrayList<Person> tmp=new ArrayList<>();
        if(persons!=null){
            for (SerializablePersona p:persons) {
                tmp.add(p.convertToPersonClass()) ;
                System.out.println(p);
            }}

        return tmp.isEmpty()?null:tmp;
    }

}
