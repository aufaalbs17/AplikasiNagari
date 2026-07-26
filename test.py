import urllib.request
import json
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

url = "https://sakoselatanpasiatalang.digitaldesa.id/berita?_data=routes/_.berita._index"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})

try:
    with urllib.request.urlopen(req, context=ctx) as response:
        data = response.read().decode('utf-8')
        print(data[:500])
        print("...")
        print(data[-500:])
        with open('data2.json', 'w') as f:
            f.write(data)
except Exception as e:
    print("Error:", e)
