
import os

source_file = 'index_clean.html'
layout_file = 'demo/src/main/resources/templates/layout.html'
home_file = 'demo/src/main/resources/templates/home/home.html'

with open(source_file, 'r', encoding='utf-8') as f:
    lines = f.readlines()

content_start_index = -1
footer_start_index = -1

for i, line in enumerate(lines):
    if '<div class="body-home">' in line:
        content_start_index = i
    if '<div id="showPromoteBHX">' in line:
        footer_start_index = i

if content_start_index == -1 or footer_start_index == -1:
    print(f"Error: Could not find split points. Content: {content_start_index}, Footer: {footer_start_index}")
    exit(1)

# Header part (for layout)
header_lines = lines[:content_start_index]
# Content part (for home)
content_lines = lines[content_start_index:footer_start_index]
# Footer part (for layout)
footer_lines = lines[footer_start_index:]

# Transform Header lines for Layout
new_header_lines = []
for line in header_lines:
    if '<html' in line:
        new_header_lines.append('<html lang="vi-VN" xmlns:th="http://www.thymeleaf.org" xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">\n')
    elif 'href="static/css/extracted_style.css"' in line:
        new_header_lines.append(line.replace('href="static/css/extracted_style.css"', 'th:href="@{/css/extracted_style.css}"'))
    elif 'src="static/js/extracted_script.js"' in line:
        new_header_lines.append(line.replace('src="static/js/extracted_script.js"', 'th:src="@{/js/extracted_script.js}"'))
    else:
        new_header_lines.append(line)

# Transform Footer lines for Layout
new_footer_lines = footer_lines # No specific changes needed usually, maybe script links if any

# Construct Layout
layout_content = "".join(new_header_lines) + '\n<div layout:fragment="content"></div>\n' + "".join(new_footer_lines)

# Construct Home
home_content = """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout" layout:decorate="~{layout}">
<head>
    <title>Thế Giới Di Động</title>
</head>
<body>
    <div layout:fragment="content">
""" + "".join(content_lines) + """
    </div>
</body>
</html>"""

# Write files
os.makedirs(os.path.dirname(layout_file), exist_ok=True)
os.makedirs(os.path.dirname(home_file), exist_ok=True)

with open(layout_file, 'w', encoding='utf-8') as f:
    f.write(layout_content)

with open(home_file, 'w', encoding='utf-8') as f:
    f.write(home_content)

print("Files generated successfully.")
