FROM python:3 as base
ADD script.py /
CMD [ "python", "./script.py" ]
