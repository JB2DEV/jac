{{- define "cv-api.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "cv-api.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- define "cv-api.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{- define "cv-api.labels" -}}
helm.sh/chart: {{ include "cv-api.chart" . }}
{{ include "cv-api.selectorLabels" . }}
app.kubernetes.io/version: {{ .Values.image.tag | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "cv-api.selectorLabels" -}}
app.kubernetes.io/name: {{ include "cv-api.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ include "cv-api.name" . }}
{{- end }}

{{- define "cv-api.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- include "cv-api.fullname" . }}-sa
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}